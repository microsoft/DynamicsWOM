package com.ttpsc.dynamics365fieldService.views

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_guide_summary.*
import kotlinx.android.synthetic.main.fragment_guide_summary.bottomBar
import kotlinx.android.synthetic.main.fragment_guide_summary.changeStatusButton
import kotlinx.android.synthetic.main.fragment_guide_summary.endTime
import kotlinx.android.synthetic.main.fragment_guide_summary.status
import kotlinx.android.synthetic.main.fragment_guide_summary.tabBar
import kotlinx.android.synthetic.main.fragment_guide_summary.titleBar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GuideSummaryFragment : Fragment() {
    private lateinit var _disposeBag: CompositeDisposable
    private lateinit var _viewModel: ProcedureDetailsViewModel
    private lateinit var _parameters: GuideSummaryFragmentArgs
    private var _dateFormat = SimpleDateFormat("dd MMM yyyy h:mm a", Locale.US)

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
        return inflater.inflate(R.layout.fragment_guide_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _disposeBag = CompositeDisposable()

        arguments?.let {
            _parameters = GuideSummaryFragmentArgs.fromBundle(it)
        }

        tabBar.setSummarySelected()
        _disposeBag += tabBar.detailsRequested().subscribe { navigate(Direction.Details) }
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
            Observer { procedure ->
                titleBar.topBarTitle = procedure.workOrder?.name ?: ""
                tabBar.setCurrentProcedure(procedure)
                bottomBar.iconicsLink = procedure.workOrder?.iconicsLink

                description.text = procedure.descriptionLines.firstOrNull()?.textRaw

                status.text = procedure.status?.let {
                    _viewModel.bookingStatuses.firstOrNull() { bookingStatus -> bookingStatus.bookingStatusId == it }?.name
                } ?: ""
                startTime.text = procedure.startTime?.let {
                    _dateFormat.format(it)
                } ?: ""
                endTime.text = procedure.endTime?.let {
                    _dateFormat.format(it)
                } ?: ""

                street1.text = procedure.workOrder?.address1 ?: ""
                street2.text = procedure.workOrder?.address2 ?: ""
                street3.text = procedure.workOrder?.address3 ?: ""

                city.text = procedure.workOrder?.city
                stateOrProvince.text = procedure.workOrder?.stateOrProvince
                postalCode.text = procedure.workOrder?.postalCode
                countryRegion.text = procedure.workOrder?.country
                serviceTerritory.text = procedure.workOrder?.serviceTerritory?.name

                pageDownButton.isEnabled = true
                pageUpButton.isEnabled = false

                bottomBar.setMoreOptionsButtonVisible(true)
            })

        _viewModel.refreshWorkOrdersCallback = _parameters.refreshWorkOrdersCallback

        if (_parameters.bookableResourceBooking != null) {
            _viewModel.bookingStatuses = _parameters.bookingStatuses!!.toList()
            _viewModel.bookableResourceBooking.value = _parameters.bookableResourceBooking
        } else if (_parameters.bookableResourceBookingId != null) {
            _disposeBag += _viewModel.getProcedure(_parameters.bookableResourceBookingId)
                .subscribe({ procedure ->
                    _viewModel.bookableResourceBooking.value = procedure
                },
                    { ex ->
                        Log.e("GET GUIDE", ex.message!!)

                        AlertDialog.Builder(requireContext()).apply {
                            setCustomTitle(
                                layoutInflater.inflate(R.layout.view_alert_dialog_title, null)
                                    .apply {
                                        findViewById<TextView>(R.id.title).text =
                                            getString(R.string.wrong_work_order)
                                    })
                            setMessage(
                                getString(
                                    R.string.cant_receive_work_order,
                                    _parameters.bookableResourceBookingId
                                )
                            )
                            setNeutralButton(android.R.string.ok) { dialog, _ ->
                                dialog.dismiss()
                            }
                        }.create().apply {
                            setOnDismissListener {
                                (activity as MainActivity).hideLoadingIndicator()
                                (activity as MainActivity).onBackPressed()
                            }
                            show()
                        }
                    })
        }

        if(_parameters.notesArray != null) {
            _viewModel.bookableResourceBooking.value?.workOrder?.notes = _parameters.notesArray!!.toMutableList()
            _viewModel.notes = _parameters.notesArray!!.toMutableList()
        }

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
                GuideSummaryFragmentDirections.actionGuideSummaryFragmentToGuidesListFragment()
            )
        }

        _disposeBag += bottomBar.goToMainMenuRequested().subscribe {
            findNavController().navigate(
                GuideSummaryFragmentDirections.actionGuideSummaryFragmentToMainMenuFragment()
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
            Direction.Details -> findNavController().navigate(
                GuideSummaryFragmentDirections.actionGuideSummaryFragmentToGuideDetailsFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Attachments -> findNavController().navigate(
                GuideSummaryFragmentDirections.actionGuideSummaryFragmentToGuideDetailsAttachmentsFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Notes -> findNavController().navigate(
            GuideSummaryFragmentDirections.actionGuideSummaryFragmentToNotesListFragment(
                procedure, statuses, callback, notes)
            )
        }

    }

    enum class Direction { Details, Attachments, Notes }
}
