package com.ttpsc.dynamics365fieldService.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.viewmodels.ProcedureDetailsViewModel
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_guide_details.*
import kotlinx.android.synthetic.main.fragment_guide_details.bottomBar
import kotlinx.android.synthetic.main.fragment_guide_details.changeStatusButton
import kotlinx.android.synthetic.main.fragment_guide_details.pageDownButton
import kotlinx.android.synthetic.main.fragment_guide_details.pageUpButton
import kotlinx.android.synthetic.main.fragment_guide_details.scrollView
import kotlinx.android.synthetic.main.fragment_guide_details.tabBar
import kotlinx.android.synthetic.main.fragment_guide_details.titleBar

import javax.inject.Inject

class GuideDetailsFragment : Fragment() {
    private lateinit var _disposeBag: CompositeDisposable
    private lateinit var _viewModel: ProcedureDetailsViewModel
    private lateinit var _parameters: GuideDetailsFragmentArgs

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
        return inflater.inflate(R.layout.fragment_guide_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _disposeBag = CompositeDisposable()

        _parameters = GuideDetailsFragmentArgs.fromBundle(requireArguments())

        tabBar.setDetailsSelected()
        _disposeBag += tabBar.summaryRequested().subscribe { navigate(Direction.Summary) }
        _disposeBag += tabBar.attachmentsRequested().subscribe { navigate(Direction.Attachments) }
        _disposeBag += tabBar.notesRequested().subscribe { navigate(Direction.Notes) }

        _viewModel.loadingIndicatorVisible.observe(viewLifecycleOwner,
            Observer { loadingIndicatorVisible ->
                if (loadingIndicatorVisible) {
                    (activity as MainActivity).showLoadingIndicator()
                } else {
                    (activity as MainActivity).hideLoadingIndicator()
                }
            })

        _viewModel.bookableResourceBooking.observe(viewLifecycleOwner,
            Observer { bookableResourceBooking ->
                titleBar.topBarTitle = bookableResourceBooking.workOrder?.name ?: ""
                tabBar.setCurrentProcedure(bookableResourceBooking)
                bottomBar.iconicsLink = bookableResourceBooking.workOrder?.iconicsLink

                var i = 0
                var tableRow: TableRow? = null
                bookableResourceBooking.customFields.forEach {
                    if (it.fieldValue.isBlank())
                        return@forEach

                    if (i++ % 3 == 0) {
                        tableRow = TableRow(requireContext())
                        tableLayout.addView(tableRow)
                    }

                    layoutInflater.inflate(R.layout.view_guide_detail, null).apply {
                        findViewById<TextView>(R.id.detailTitle).text = it.fieldDescription
                        findViewById<TextView>(R.id.detailValue).text = it.fieldValue
                        tableRow?.addView(this)
                    }
                }

                pageUpButton.isEnabled = false
                scrollView.viewTreeObserver.addOnGlobalLayoutListener(
                    object: ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            pageDownButton.isEnabled = scrollView.canScrollVertically(1)
                            scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                )

                bottomBar.setMoreOptionsButtonVisible(true)
            })

        _viewModel.refreshWorkOrdersCallback = _parameters.refreshWorkOrdersCallback

        _viewModel.errorPopupVisible.observe(viewLifecycleOwner,
            Observer { errorPopupVisible ->
                if (errorPopupVisible) {
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle(android.R.string.dialog_alert_title)
                        setMessage(_viewModel.errorMessage)
                        setPositiveButton(android.R.string.yes) { dialog, which ->
                            dialog.dismiss()
                        }
                    }.create().show()
                    _viewModel.errorPopupVisible.value = false
                }
            })

        _disposeBag += bottomBar.goToWorkOrdersRequested().subscribe {
            findNavController().navigate(
                GuideDetailsFragmentDirections.actionGuideDetailsFragmentToGuidesListFragment()
            )
        }

        _disposeBag += bottomBar.goToMainMenuRequested().subscribe {
            findNavController().navigate(
                GuideDetailsFragmentDirections.actionGuideDetailsFragmentToMainMenuFragment()
            )
        }

        _disposeBag += bottomBar.errorRequested().subscribe { errorMessage ->
            errorMessage?.let {
                _viewModel.errorMessage = it
                _viewModel.errorPopupVisible.value = true
            }
        }

        // TODO This functionality should be somehow inherited for multiple viewModels
        _disposeBag += changeStatusButton.clicks()
            .flatMap {
                if (_viewModel.bookingStatuses.count() > 0) {
                    Observable.just(_viewModel.bookingStatuses)
                } else {
                    _viewModel.fetchStatuses()
                }
            }
            .subscribe { bookingStatuses ->
                val statusesOptions =
                    bookingStatuses.map { bookingStatus -> bookingStatus.name }.toTypedArray()
                val currentStatus = bookingStatuses.indexOf(bookingStatuses.find { bookingStatus ->
                    bookingStatus.bookingStatusId == _viewModel.bookableResourceBooking.value!!.status
                })
                AlertDialog.Builder(requireContext()).apply {
                    setSingleChoiceItems(statusesOptions, currentStatus) { dialog, clickedIndex ->
                        val selectedStatus = bookingStatuses[clickedIndex]
                        _viewModel.changeStatus(
                            selectedStatus,
                            _viewModel.bookableResourceBooking.value!!.id,
                            requireContext()
                        )
                        dialog.dismiss()
                    }
                    setCustomTitle(
                        layoutInflater.inflate(R.layout.view_alert_dialog_title, null).apply {
                            findViewById<TextView>(R.id.title).text =
                                getString(R.string.set_status_to)
                        })
                    setNeutralButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                }.create().show()
            }

        _disposeBag += pageDownButton.clicks().subscribe { scrollView.pageScroll(View.FOCUS_DOWN) }
        _disposeBag += pageUpButton.clicks().subscribe { scrollView.pageScroll(View.FOCUS_UP) }

        scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            pageUpButton.isEnabled = scrollView.canScrollVertically(-1)
            pageDownButton.isEnabled = scrollView.canScrollVertically(1)
        }

        _viewModel.bookableResourceBooking.value =
            _parameters.bookableResourceBooking
        _viewModel.bookingStatuses = _parameters.bookingStatuses.toList()
        if(_parameters.notesArray != null) {
            _viewModel.bookableResourceBooking.value?.workOrder?.notes = _parameters.notesArray!!.toMutableList()
            _viewModel.notes = _parameters.notesArray!!.toMutableList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _disposeBag.dispose()
    }

    private fun navigate(direction : Direction) {
        if (_viewModel.bookableResourceBooking.value == null || _viewModel.bookingStatuses.isEmpty())
            return

        val procedure = _viewModel.bookableResourceBooking.value!!
        val statuses = _viewModel.bookingStatuses.toTypedArray()
        val callback = _viewModel.refreshWorkOrdersCallback
        val notes = _viewModel.bookableResourceBooking.value?.workOrder!!.notes.toTypedArray()

        when (direction) {
            Direction.Summary -> findNavController().navigate(
                GuideDetailsFragmentDirections.actionGuideDetailsFragmentToGuideSummaryFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Attachments -> findNavController().navigate(
                GuideDetailsFragmentDirections.actionGuideDetailsFragmentToGuideDetailsAttachmentsFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Notes -> findNavController().navigate(
                GuideDetailsFragmentDirections.actionGuideDetailsFragmentToNotesListFragment(
                    procedure, statuses, callback, notes)
            )
        }

    }

    enum class Direction { Summary, Attachments, Notes }
}