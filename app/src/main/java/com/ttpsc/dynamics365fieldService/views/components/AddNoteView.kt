package com.ttpsc.dynamics365fieldService.views.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.core.content.res.getTextOrThrow
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.getActivity
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.File
import java.util.*

class AddNoteView: LinearLayout {
    private lateinit var _addCommentButton: Button
    private lateinit var _addPhotoButton: Button
    private lateinit var _addVideoButton: Button

    private lateinit var _cancelButton: View
    private lateinit var _confirmButton: View

    private lateinit var _noteAddedView: View
    private lateinit var _photoAddedView: View
    private lateinit var _videoAddedView: View

    private lateinit var _disposeBag: CompositeDisposable
    private val _addNoteSubject = PublishSubject.create<Unit>()
    private val _addPhotoSubject = PublishSubject.create<Unit>()
    private val _addVideoSubject = PublishSubject.create<Unit>()
    private val _confirmSubject = PublishSubject.create<Unit>()
    private val _cancelSubject = PublishSubject.create<Unit>()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        init(context)
    }

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
            : super(context, attributes, defStyleAttr) {
        init(context)
    }

    fun commentAddRequested() = _addNoteSubject
    fun photoAddRequested() = _addPhotoSubject
    fun videoAddRequested() = _addVideoSubject
    fun addNoteRequested() = _confirmSubject
    fun cancelRequested() = _cancelSubject

    fun setCommentAddedState(state: Boolean){
        _noteAddedView.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    fun setPhotoAddedState(state: Boolean){
        _photoAddedView.visibility = if (state) View.VISIBLE else View.INVISIBLE
        if (state) {
            _addVideoButton.isEnabled = false
        }
    }

    fun setVideoAddedState(state: Boolean){
        _videoAddedView.visibility = if (state) View.VISIBLE else View.INVISIBLE
        if (state) {
            _addPhotoButton.isEnabled = false
        }
    }

    private fun resetView() {
        _noteAddedView.visibility = View.INVISIBLE
        _videoAddedView.visibility = View.INVISIBLE
        _photoAddedView.visibility = View.INVISIBLE
        _addVideoButton.isEnabled = true
        _addPhotoButton.isEnabled = true
    }

    private fun init(context: Context) {
        _disposeBag = CompositeDisposable()
        LayoutInflater.from(context).inflate(R.layout.view_add_note, this, true)
        _confirmButton = findViewById(R.id.confirmNoteButton)
        _cancelButton = findViewById(R.id.cancelAddingNotesButton)

        _addCommentButton = findViewById(R.id.addCommentButton)
        _addPhotoButton = findViewById(R.id.addPhotoButton)
        _addVideoButton = findViewById(R.id.addVideoButton)

        _noteAddedView = findViewById(R.id.addedCommentView)
        _photoAddedView = findViewById(R.id.addedPhotoView)
        _videoAddedView =findViewById(R.id.addedVideoView)
        bindEvents()
    }

    private fun bindEvents(){
        _disposeBag += _addCommentButton.clicks().subscribe{
            _addNoteSubject.onNext(Unit)
        }

        _disposeBag += _addPhotoButton.clicks().subscribe{
            _addPhotoSubject.onNext(Unit)
        }

        _disposeBag += _addVideoButton.clicks().subscribe{
            _addVideoSubject.onNext(Unit)
        }

        _disposeBag += _cancelButton.clicks().subscribe {
            resetView()
            visibility = View.INVISIBLE
            _cancelSubject.onNext(Unit)
        }

        _disposeBag += _confirmButton.clicks().subscribe {
            resetView()
            visibility = View.INVISIBLE
            _confirmSubject.onNext(Unit)
        }
    }


}