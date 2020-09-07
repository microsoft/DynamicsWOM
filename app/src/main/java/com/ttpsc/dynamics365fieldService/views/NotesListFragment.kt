package com.ttpsc.dynamics365fieldService.views

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.bll.models.Note
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.viewmodels.ProcedureDetailsViewModel
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import com.ttpsc.dynamics365fieldService.views.activities.RecordingActivity
import com.ttpsc.dynamics365fieldService.views.adapters.NotesListAdapter
import com.ttpsc.dynamics365fieldService.views.components.NoteListItem
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_guide_details_notes.*
import kotlinx.android.synthetic.main.fragment_guide_details_notes.bottomBar
import kotlinx.android.synthetic.main.fragment_guide_details_notes.changeStatusButton
import kotlinx.android.synthetic.main.fragment_guide_details_notes.tabBar
import kotlinx.android.synthetic.main.fragment_guide_details_notes.titleBar
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject

class NotesListFragment : Fragment() {

    companion object {
        private const val BASIC_CAMERA_REQUEST_CODE = 1889
        private const val BASIC_VIDEO_REQUEST_CODE = 1890
        private const val BASIC_DICTATION_REQUEST_CODE = 1891
        private const val CAMERA_PERMISSION_REQUEST_CODE_TAKE_PHOTO = 1892
        private const val CAMERA_PERMISSION_REQUEST_CODE_RECORD_VIDEO = 1893
    }

    private var _photo: File? = null
    private var _video: File? = null
    private var _compressedVideo: String? = null
    private var _noteContent: String? = null
    private var _externalFileDir: File? = null
    private var _notesReloaded = false

    private lateinit var _disposeBag: CompositeDisposable
    private lateinit var _viewModel: ProcedureDetailsViewModel
    private lateinit var _parameters: NotesListFragmentArgs

    private var _adapter: NotesListAdapter? = null

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
        return inflater.inflate(R.layout.fragment_guide_details_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _externalFileDir = requireContext().getExternalFilesDir(null)
        _disposeBag = CompositeDisposable()
        _parameters = NotesListFragmentArgs.fromBundle(requireArguments())
        _viewModel.bookableResourceBooking.value = _parameters.bookableResourceBooking
        _viewModel.bookingStatuses = _parameters.bookingStatuses.toList()

        tabBar.setNotesSelected()
        _disposeBag += tabBar.summaryRequested().subscribe { navigate(Direction.Summary) }
        _disposeBag += tabBar.detailsRequested().subscribe { navigate(Direction.Details) }
        _disposeBag += tabBar.attachmentsRequested().subscribe { navigate(Direction.Attachments) }
        tabBar.setCurrentProcedure(_viewModel.bookableResourceBooking.value!!)

        titleBar.topBarTitle = _parameters.bookableResourceBooking.workOrder?.name ?: ""
        bottomBar.setMoreOptionsButtonVisible(true)

        if(_notesReloaded == false) {
            _viewModel.notes =
                _parameters.notesArray?.toMutableList() ?: mutableListOf()
        }
        _viewModel.initializeList()
        _viewModel.refreshWorkOrdersCallback = _parameters.refreshWorkOrdersCallback
        _viewModel.previousPageVisible.observe(
            viewLifecycleOwner,
            Observer { visible ->
                activity?.runOnUiThread {
                    previousPageButton.isEnabled = visible
                }
            }
        )

        _viewModel.nextPageVisible.observe(
            viewLifecycleOwner,
            Observer { visible ->
                activity?.runOnUiThread {
                    nextPageButton.isEnabled = visible
                }
            }
        )

        _viewModel.currentPageIndex.observe(
            viewLifecycleOwner,
            Observer {
                val firstIndexOnPage = _viewModel.getFirstIndexOnCurrentPage()
                val lastIndexOnPage = _viewModel.getLastIndexOnCurrentPage()
                if (lastIndexOnPage == 0) {
                    selectNoteButton.visibility = View.GONE
                    openAttachmentButton.visibility = View.GONE
                    return@Observer
                }

                selectNoteButton.visibility = View.VISIBLE
                openAttachmentButton.visibility = View.VISIBLE
                if (firstIndexOnPage == lastIndexOnPage) {
                    selectNoteButton.text = getString(R.string.select_note, firstIndexOnPage)
                    openAttachmentButton.text =
                        getString(R.string.show_attachment, firstIndexOnPage)
                }
                else {
                    selectNoteButton.text =
                        getString(R.string.select_note_multiple, firstIndexOnPage, lastIndexOnPage)
                    openAttachmentButton.text = getString(R.string.show_attachment_multiple, firstIndexOnPage, lastIndexOnPage)

                }
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
            Observer { notes ->
                if (notes != null) {
                    val notesDataList = mutableListOf<NoteListItem>()
                    for (note: Note in notes) {
                        notesDataList.add(
                            NoteListItem(
                                note.id,
                                note.subject,
                                note.userInfo?.fullName,
                                note.date,
                                note.isDocument
                            )
                        )
                    }

                    if (_adapter == null) {
                        _adapter = NotesListAdapter(notesDataList)
                        _disposeBag += _adapter!!.onItemClick()
                            .subscribe {
                                val note = _viewModel.notes.firstOrNull { note ->
                                    note.id == it
                                }
                                findNavController().navigate(
                                    NotesListFragmentDirections.actionNotesListFragmentToNoteDetailsFragment(
                                        note!!
                                    )
                                )
                            }
                        _disposeBag += _adapter!!.showAttachment()
                            .subscribe {
                                val note = _viewModel.notes.firstOrNull { note ->
                                    note.id == it
                                }
                                if (note != null)
                                {
                                    _viewModel.openNoteAttachment(note.id!!, requireContext())
                                }
                            }
                    } else {
                        _adapter!!.updateData(
                            notesDataList,
                            _viewModel.getFirstIndexOnCurrentPage()
                        )
                    }

                    if (notesList.adapter == null) {
                        notesList.apply {
                            setHasFixedSize(true)
                            adapter = _adapter
                        }
                    }

                    _disposeBag += _adapter!!

                }

                selectNoteButton.visibility = if (notes != null) View.VISIBLE else View.GONE
            })

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

        _disposeBag += previousPageButton.clicks()
            .subscribe {
                _viewModel.setPreviousPage()
            }

        _disposeBag += nextPageButton.clicks()
            .subscribe {
                _viewModel.setNextPage()
            }


        _disposeBag += addNoteButton.clicks().subscribe {
            addNoteView.visibility = View.VISIBLE
        }

        notesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        _disposeBag += bottomBar.goToWorkOrdersRequested().subscribe {
            findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToGuidesListFragment()
            )
        }

        _disposeBag += bottomBar.goToMainMenuRequested().subscribe {
            findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToMainMenuFragment()
            )
        }

        _disposeBag += bottomBar.errorRequested().subscribe {errorMessage ->
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

        bindAddingNoteEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _disposeBag.dispose()
        _adapter = null
    }

    private fun reloadNotes() {
        (activity as MainActivity).showLoadingIndicator()
        _disposeBag += _viewModel.getNotes().execute()
            .subscribe {
                _viewModel.notes = it.toMutableList()
                _viewModel.bookableResourceBooking.value?.workOrder?.notes = it.toMutableList()
                _viewModel.initializeList()
                _notesReloaded = true
            }
    }

    private fun bindAddingNoteEvents() {
        _disposeBag += addNoteView.addNoteRequested().flatMap {
            (activity as MainActivity).showLoadingIndicator()

            _viewModel.createNoteOperation.apply {
                workOrderId = _viewModel.bookableResourceBooking.value!!.workOrderId
            }

            if (_noteContent != null) {
                _viewModel.createNoteOperation.apply {
                    subject = _noteContent
                }
            }

            if (_photo != null) {

                return@flatMap getBytesFromFile(_photo!!).observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io()).map { Pair(_photo!!.name, it) }
            } else if (_video != null) {
                return@flatMap getBytesFromFile(_video!!).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map { Pair(
                    _video!!.name, it) }
            }
            return@flatMap Observable.just(Pair("", ByteArray(0)))
        }.flatMap {
            if (it.second.isNotEmpty()) {
                _viewModel.createNoteOperation.apply {
                    documentBody = it.second.toTypedArray()
                    filename = it.first
                }
            }

            _viewModel.createNoteOperation.execute().observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())

        }.observeOn(AndroidSchedulers.mainThread()).subscribe({
            if(it.first == true && it.second.isNullOrEmpty() == false){
                _viewModel.errorMessage = it.second!!
                _viewModel.errorPopupVisible.value = true
            }
            reloadNotes()
            resetAddingState()
        }, {
            (activity as MainActivity).hideLoadingIndicator()
            resetAddingState()
        })

        _disposeBag += addNoteView.photoAddRequested().subscribe {
            takePhoto()
        }

        _disposeBag += addNoteView.videoAddRequested().subscribe {
            recordVideo()
        }

        _disposeBag += addNoteView.commentAddRequested().subscribe {
            runDictation()
        }

        _disposeBag += addNoteView.cancelRequested().subscribe {
            resetAddingState()
        }
    }

    private fun getBytesFromFile(file: File): Observable<ByteArray> {
        return Observable.create {
            val inputStream: InputStream = FileInputStream(file)
            it.onNext(inputStream.readBytes())
            it.onComplete()
        }
    }

    private fun takePhoto() {
        if (checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE_TAKE_PHOTO)
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        _photo = File(
            _externalFileDir, "pic${UUID.randomUUID().toString()
                .substring(8)}.jpg"
        )
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                _photo!!
            )
        )
        startActivityForResult(intent, BASIC_CAMERA_REQUEST_CODE)
    }

    private fun recordVideo() {
        if (checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED
            || checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                CAMERA_PERMISSION_REQUEST_CODE_RECORD_VIDEO)
            return
        }

        _video = File(
            _externalFileDir, "vid${UUID.randomUUID().toString()
                .substring(8)}.mp4"
        )

        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".provider",
            _video!!
        )

        startActivityForResult(
            Intent(context, RecordingActivity::class.java)
                .putExtra(RecordingActivity.EXTRA_OUTPUT, uri),
            BASIC_VIDEO_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
            AlertDialog.Builder(requireContext()).apply {
                setCustomTitle(
                    layoutInflater.inflate(R.layout.view_alert_dialog_title, null)
                        .apply {
                            findViewById<TextView>(R.id.title).text =
                                getString(R.string.permissions_required_title)
                        })
                setMessage(getString(R.string.permissions_required_message))
                setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            }.create().apply {
                show()
            }
            return
        }

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE_TAKE_PHOTO -> takePhoto()
            CAMERA_PERMISSION_REQUEST_CODE_RECORD_VIDEO -> recordVideo()
        }
    }

    private fun runDictation() {
        val intent = Intent("com.realwear.keyboard.intent.action.DICTATION")
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, BASIC_DICTATION_REQUEST_CODE)
        } else {
            onActivityResult(
                BASIC_DICTATION_REQUEST_CODE,
                Activity.RESULT_OK,
                Intent().putExtra("result", "TEMP_TEXT")
            )
        }
    }

    private fun resetAddingState() {
        _photo = null
        _video = null
        _compressedVideo = null
        _viewModel.createNoteOperation.apply {
            documentBody = null
            subject = null
            filename = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                BASIC_CAMERA_REQUEST_CODE -> {
                    addNoteView.setPhotoAddedState(true)
                }

                BASIC_VIDEO_REQUEST_CODE -> {
                    addNoteView.setVideoAddedState(true)
                }

                BASIC_DICTATION_REQUEST_CODE -> {
                    if (data == null) return
                    _noteContent = data.getStringExtra("result")
                    addNoteView.setCommentAddedState(true)
                }
            }
        }

        if (resultCode == Activity.RESULT_CANCELED) {
            when (requestCode) {
                BASIC_CAMERA_REQUEST_CODE -> {
                    addNoteView.setVideoAddedState(false)
                }

                BASIC_VIDEO_REQUEST_CODE -> {
                    addNoteView.setVideoAddedState(false)
                }

                BASIC_DICTATION_REQUEST_CODE -> {
                    addNoteView.setCommentAddedState(false)
                }
            }
        }
    }

    private fun navigate(direction : Direction) {
        if (_viewModel.bookableResourceBooking.value == null || _viewModel.bookingStatuses.isEmpty())
            return

        _viewModel.notes.toTypedArray()
        val procedure = _viewModel.bookableResourceBooking.value!!
        val statuses = _viewModel.bookingStatuses.toTypedArray()
        val callback = _viewModel.refreshWorkOrdersCallback
        val notes = _viewModel.notes.toTypedArray()

        when (direction) {
            Direction.Summary -> findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToGuideSummaryFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Details -> findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToGuideDetailsFragment(
                    procedure, statuses, callback, notes)
            )
            Direction.Attachments -> findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToGuideDetailsAttachmentsFragment(
                    procedure, statuses, callback, notes)
            )
        }

    }

    enum class Direction { Summary, Details, Attachments}
}