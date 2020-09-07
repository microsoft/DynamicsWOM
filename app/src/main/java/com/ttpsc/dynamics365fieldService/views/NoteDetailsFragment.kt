package com.ttpsc.dynamics365fieldService.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ttpsc.dynamics365fieldService.AndroidApplication
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.helpers.files.FileUtility
import com.ttpsc.dynamics365fieldService.viewmodels.NoteDetailsViewModel
import com.ttpsc.dynamics365fieldService.views.activities.MainActivity
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.viewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_note_details.*
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

class NoteDetailsFragment : Fragment() {
    private lateinit var _disposeBag: CompositeDisposable
    private lateinit var _viewModel: NoteDetailsViewModel
    private lateinit var navigationParameters: NoteDetailsFragmentArgs

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
        return inflater.inflate(R.layout.fragment_note_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _disposeBag = CompositeDisposable()
        navigationParameters = NoteDetailsFragmentArgs.fromBundle(requireArguments())
        _viewModel.note.value = navigationParameters.note

        _viewModel.note.observe(viewLifecycleOwner,
            Observer { note ->
                ownerValueTextView.text = note.userInfo?.fullName ?: ""
                dateValueTextView.text = note.date.let {
                    SimpleDateFormat("dd MMM yyyy HH:mm z").format(it)
                }

                noteContentTextView.text = note.subject
                showAttachmentButton.visibility = if (note.isDocument) View.VISIBLE else View.INVISIBLE
            })

        _disposeBag += showAttachmentButton.clicks()
            .flatMap {
                (activity as MainActivity).showLoadingIndicator()
                _viewModel.fetchAttachment(_viewModel.note.value!!.id!!)
            }
            .subscribe({
                val file =
                    File(requireContext().getExternalFilesDir(null).toString(), it.fileName ?: "")
                file.writeBytes(it.documentBody.toByteArray())
                FileUtility.openWithDefaultApp(requireContext(), file)
                (activity as MainActivity).hideLoadingIndicator()
            }, { ex ->
                (activity as MainActivity).hideLoadingIndicator()
                Log.e("FETCH ATTACHMENT", ex.message!!)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _disposeBag.dispose()
    }
}