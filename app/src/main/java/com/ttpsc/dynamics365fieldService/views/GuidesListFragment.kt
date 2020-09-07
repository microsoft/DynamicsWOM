package com.ttpsc.dynamics365fieldService.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.bll.models.BookableResourceBooking
import com.ttpsc.dynamics365fieldService.bll.models.Note
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.helpers.RefreshWorkOrdersCallback
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.helpers.models.SortingType
import com.ttpsc.dynamics365fieldService.viewmodels.ProcedureListViewModel
import com.ttpsc.dynamics365fieldService.views.adapters.GuidesListAdapter
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import com.ttpsc.dynamics365fieldService.views.components.GuidesListItem
import com.ttpsc.dynamics365fieldService.views.enums.ListItemStatus
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_guides_list.*
import kotlinx.android.synthetic.main.fragment_guides_list.nextPageButton
import kotlinx.android.synthetic.main.fragment_guides_list.previousPageButton
import kotlinx.android.synthetic.main.sort_list_popup_view.view.*
import kotlinx.android.synthetic.main.view_alert_dialog_title.*
import java.lang.Exception
import javax.inject.Inject

class GuidesListFragment : Fragment() {
    private lateinit var _disposeBag: CompositeDisposable
    private lateinit var _viewModel: ProcedureListViewModel
    private var _reloadListCallback = RefreshWorkOrdersCallback()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidApplication.companionAppComponent?.inject(this)
        _viewModel = viewModel(viewModelFactory) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guides_list, container, false)

        view.findViewById<RecyclerView>(R.id.guidesList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter =  GuidesListAdapter().apply {
                onItemClick().subscribe { itemId ->
                    findNavController().navigate(
                        GuidesListFragmentDirections.actionGuidesListFragmentToGuideSummaryGraph(
                            itemId, null, null, _reloadListCallback, null
                        )
                    )
                }
            }
            setHasFixedSize(true)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _disposeBag = CompositeDisposable()

        _reloadListCallback.refreshList.subscribe {
            _viewModel.listNeedsReload = true
        }

        _viewModel.previousPageVisible.observe(
            viewLifecycleOwner,
            Observer { visible ->
                previousPageButton.isEnabled = visible
            }
        )

        _viewModel.nextPageVisible.observe(
            viewLifecycleOwner,
            Observer { visible ->
                nextPageButton.isEnabled = visible
            }
        )

        _viewModel.loadingIndicatorVisible.observe(viewLifecycleOwner,
            Observer { loadingIndicatorVisible ->
                if (loadingIndicatorVisible) {
                    (activity as MainActivity).showLoadingIndicator()
                } else {
                    (activity as MainActivity).hideLoadingIndicator()
                }
            })

        _viewModel.currentPageList.observe(viewLifecycleOwner,
            Observer { procedures ->
                if (procedures != null && procedures.isNotEmpty()) {
                    guidesList.visibility = View.VISIBLE
                    refreshPrompt.visibility = View.GONE
                    selectBookingTextView.visibility = View.VISIBLE
                    sortButton.visibility = View.VISIBLE
                    nextPageButton.visibility = View.VISIBLE
                    previousPageButton.visibility = View.VISIBLE

                    val guidesDataList = procedures.map { procedure ->
                        val status = procedure.status?.let {
                            _viewModel.bookingStatuses.firstOrNull { bookingStatus -> bookingStatus.bookingStatusId == it }
                        } ?: _viewModel.bookingStatuses[0]

                        GuidesListItem(
                            procedure.id,
                            procedure.title ?: getString(R.string.no_work_order_attached),
                            procedure.workOrder?.name,
                            procedure.endTime,
                            status.name,
                            when (status) {
                                _viewModel.bookingStatuses[0] -> ListItemStatus.NONE
                                _viewModel.bookingStatuses[1] -> ListItemStatus.IN_PROGRESS
                                _viewModel.bookingStatuses[2] -> ListItemStatus.DONE
                                else -> ListItemStatus.NONE
                            }
                        )
                    }

                    (guidesList.adapter as GuidesListAdapter).updateData(
                        guidesDataList,
                        _viewModel.getFirstIndexOnCurrentPage() + 1
                    )
                }
                else {
                    guidesList.visibility = View.INVISIBLE
                    refreshPrompt.visibility = View.VISIBLE
                    selectBookingTextView.visibility = View.INVISIBLE
                    sortButton.visibility = View.INVISIBLE
                    nextPageButton.visibility = View.INVISIBLE
                    previousPageButton.visibility = View.INVISIBLE
                }

                val firstIndexOnPage = _viewModel.getFirstIndexOnCurrentPage() + 1
                val lastIndexOnPage = _viewModel.getLastIndexOnCurrentPage() + 1
                if (firstIndexOnPage == lastIndexOnPage) {
                    selectBookingTextView.text = getString(R.string.select_guide, firstIndexOnPage)
                }
                else {
                    selectBookingTextView.text =
                        getString(R.string.select_guide_multiple, firstIndexOnPage, lastIndexOnPage)
                }

            })

        _disposeBag += refreshButton.clicks()
            .subscribe {
                refreshPrompt.visibility = View.GONE
                _viewModel.initializeList()
            }

        _disposeBag += previousPageButton.clicks()
            .subscribe {
                _viewModel.setPreviousPage()
            }

        _disposeBag += nextPageButton.clicks()
            .subscribe {
                _viewModel.setNextPage()
            }

        _disposeBag += sortButton.clicks()
            .subscribe {
                showSortPopup()
            }

        _viewModel.sortAscending.value = false
        _viewModel.sortBy.value = SortingType.END_TIME

        if (_viewModel.bookableResourcesBookings == null || _viewModel.listNeedsReload) {
            _viewModel.initializeList()
            _viewModel.listNeedsReload = false
        }
    }

    private fun showSortPopup() {
        val titleView = layoutInflater.inflate(R.layout.view_alert_dialog_title, null)
        val contentView = layoutInflater.inflate(R.layout.sort_list_popup_view, null)

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setCustomTitle(titleView)
            setView(contentView)
        }.create()

        titleView.findViewById<TextView>(R.id.title).text =
            getString(R.string.sort_work_orders)

        contentView.findViewById<RadioGroup>(R.id.radioGroup).apply {
            workOrder.isChecked = _viewModel.sortBy.value == SortingType.WORK_ORDER
            status.isChecked = _viewModel.sortBy.value == SortingType.STATUS
            endTime.isChecked = _viewModel.sortBy.value == SortingType.END_TIME
            setOnCheckedChangeListener { _, selectedIndex ->
                _viewModel.sortBy.value = when (selectedIndex) {
                    R.id.workOrder -> SortingType.WORK_ORDER
                    R.id.status -> SortingType.STATUS
                    R.id.endTime -> SortingType.END_TIME
                    else -> SortingType.END_TIME
                }
                _viewModel.initializeList()
                alertDialog.dismiss()
            }
        }

        contentView.findViewById<Switch>(R.id.descendingSwitch).apply {
            isChecked = !_viewModel.sortAscending.value!!
            setOnCheckedChangeListener { buttonView, isChecked ->
                _viewModel.sortAscending.value = !isChecked
                _viewModel.initializeList()
                alertDialog.dismiss();
            }
        }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _disposeBag.dispose()
    }
}
