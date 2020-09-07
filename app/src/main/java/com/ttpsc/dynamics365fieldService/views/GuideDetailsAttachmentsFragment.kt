package com.ttpsc.dynamics365fieldService.views

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.android.synthetic.main.fragment_guide_details_attachments.*
import kotlinx.android.synthetic.main.fragment_guide_details_attachments.changeStatusButton
import kotlinx.android.synthetic.main.fragment_guide_details_attachments.tabBar
import kotlinx.android.synthetic.main.fragment_guide_details_attachments.titleBar
import javax.inject.Inject

class GuideDetailsAttachmentsFragment : Fragment() {
    private lateinit var _disposeBag: CompositeDisposable
    private lateinit var _viewModel: ProcedureDetailsViewModel
    private lateinit var _parameters: GuideDetailsAttachmentsFragmentArgs

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
        return inflater.inflate(R.layout.fragment_guide_details_attachments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _disposeBag = CompositeDisposable()

        _parameters = GuideDetailsAttachmentsFragmentArgs.fromBundle(requireArguments())

        tabBar.setAttachmentsSelected()
        _disposeBag += tabBar.summaryRequested().subscribe { navigate(Direction.Summary) }
        _disposeBag += tabBar.detailsRequested().subscribe { navigate(Direction.Details) }
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

                setIconAndButtonState(
                    bookableResourceBooking.workOrder?.documentAttachmentUrl,
                    showPdfButton,
                    documentIcon
                )
                setIconAndButtonState(
                    bookableResourceBooking.workOrder?.imageAttachmentUrl,
                    showImageButton,
                    imageIcon
                )
                setIconAndButtonState(
                    bookableResourceBooking.workOrder?.videoAttachmentUrl,
                    showVideoButton,
                    videoIcon
                )

                bottomBar.setMoreOptionsButtonVisible(true)
            })

        _viewModel.refreshWorkOrdersCallback = _parameters.refreshWorkOrdersCallback
        if (_parameters.notesArray != null) {
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
                GuideDetailsAttachmentsFragmentDirections.actionGuideDetailsAttachmentsFragmentToGuidesListFragment()
            )
        }

        _disposeBag += bottomBar.goToMainMenuRequested().subscribe {
            findNavController().navigate(
                GuideDetailsAttachmentsFragmentDirections.actionGuideDetailsAttachmentsFragmentToMainMenuFragment()
            )
        }

        _disposeBag += bottomBar.errorRequested().subscribe { errorMessage ->
            errorMessage?.let {
                _viewModel.errorMessage = it
                _viewModel.errorPopupVisible.value = true
            }
        }

        _disposeBag += showPdfButton.clicks()
            .subscribe {
                _viewModel.openDocumentAttachment(requireContext())
            }

        _disposeBag += showImageButton.clicks()
            .subscribe {
                _viewModel.openImageAttachment(requireContext())
            }

        _disposeBag += showVideoButton.clicks()
            .subscribe {
                _viewModel.openVideoAttachment(requireContext())
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

        _viewModel.bookableResourceBooking.value =
            _parameters.bookableResourceBooking

        _viewModel.bookingStatuses = _parameters.bookingStatuses.toList()
    }

    private fun setIconAndButtonState(url: String?, button: Button, image: ImageView) {
        if (url.isNullOrEmpty()) {
            image.setBackgroundTintList(
                requireContext().getResources()
                    .getColorStateList(R.color.color_dark_blue_opacity_01)
            )
            button.isEnabled = false
            button.setBackgroundTintList(
                requireContext().getResources()
                    .getColorStateList(R.color.color_dark_blue_opacity_01)
            )
        } else {
            image.setBackgroundTintList(
                requireContext().getResources()
                    .getColorStateList(R.color.dark_blue)
            )
            button.isEnabled = true
            button.setBackgroundTintList(
                requireContext().getResources().getColorStateList(R.color.dark_blue)
            )
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
                GuideDetailsAttachmentsFragmentDirections.actionGuideDetailsAttachmentsFragmentToGuideSummaryFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Details -> findNavController().navigate(
                GuideDetailsAttachmentsFragmentDirections.actionGuideDetailsAttachmentsFragmentToGuideDetailsFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Notes -> findNavController().navigate(
                GuideDetailsAttachmentsFragmentDirections.actionGuideDetailsAttachmentsFragmentToNotesListFragment(
                    procedure, statuses, callback, notes)
            )
        }

    }

    enum class Direction { Summary, Details, Notes }
}
