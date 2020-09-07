package com.ttpsc.dynamics365fieldService.views.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getTextOrThrow
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.bll.models.Procedure
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import com.ttpsc.dynamics365fieldService.views.extensions.toInt
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_tab_bar.view.*
import kotlinx.android.synthetic.main.view_top_bar.view.*

class TabBarView : ConstraintLayout {

    private lateinit var _disposeBag: CompositeDisposable
    private val _summarySubject = PublishSubject.create<Unit>()
    private val _detailsSubject = PublishSubject.create<Unit>()
    private val _attachmentsSubject = PublishSubject.create<Unit>()
    private val _notesSubject = PublishSubject.create<Unit>()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        init(context, attributes)
    }

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
            : super(context, attributes, defStyleAttr) {
        init(context, attributes)
    }

    private fun init(context: Context, attributes: AttributeSet? = null) {
        LayoutInflater.from(context).inflate(R.layout.view_tab_bar, this, true)

        _disposeBag = CompositeDisposable()

        _disposeBag += buttonSummary.clicks().subscribe{ _summarySubject.onNext(Unit) }
        _disposeBag += buttonDetails.clicks().subscribe{ _detailsSubject.onNext(Unit) }
        _disposeBag += buttonAttachments.clicks().subscribe{ _attachmentsSubject.onNext(Unit) }
        _disposeBag += buttonNotes.clicks().subscribe { _notesSubject.onNext(Unit) }
    }

    fun summaryRequested() = _summarySubject
    fun detailsRequested() = _detailsSubject
    fun attachmentsRequested() = _attachmentsSubject
    fun notesRequested() = _notesSubject

    fun setSummarySelected() = setSelected(buttonSummary)
    fun setDetailsSelected() = setSelected(buttonDetails)
    fun setAttachmentsSelected() = setSelected(buttonAttachments)
    fun setNotesSelected() = setSelected(buttonNotes)

    private fun setSelected(view : View) {
        view.isClickable = false
        view.setBackgroundResource(R.color.tab_bar_background_selected)
    }

    fun setCurrentProcedure(procedure : Procedure) {
        val hasDocument = procedure.workOrder?.documentAttachmentUrl != null
        val hasVideo = procedure.workOrder?.imageAttachmentUrl != null
        val hasPhoto = procedure.workOrder?.videoAttachmentUrl != null
        buttonAttachments.isEnabled = hasDocument || hasVideo || hasPhoto

        val attachmentsCount = hasDocument.toInt() + hasVideo.toInt() + hasPhoto.toInt()
        if (attachmentsCount > 0)
            textAttachments.text = context.getString(R.string.guides_tab_attachments_count, attachmentsCount)

        procedure.workOrder?.notes?.let {
            if (it.isNotEmpty())
                textNotes.text = context.getString(R.string.guides_tab_notes_count, it.size)
        }
    }
}