package com.ttpsc.dynamics365fieldService.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.views.components.NoteListItem
import com.ttpsc.dynamics365fieldService.views.components.NotesListItemViewHolder
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class NotesListAdapter(private var listItems: MutableList<NoteListItem>): RecyclerView.Adapter<NotesListItemViewHolder?>(), Disposable {
    private val clickSubject = PublishSubject.create<String>()
    private val showAttachmentSubject = PublishSubject.create<String>()
    private val disposeBag = CompositeDisposable()
    private lateinit var context: Context
    private var startIndex = 1
    private var dateFormat =  SimpleDateFormat("dd MMM yyyy h:mm a", Locale.US)


    fun onItemClick() = clickSubject
    fun showAttachment() = showAttachmentSubject

    fun updateData(items: List<NoteListItem>, startIndex: Int) {
        this.startIndex = startIndex

        listItems.clear()
        listItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListItemViewHolder {
        context = parent.context

        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.notes_list_item, parent, false)

        return NotesListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: NotesListItemViewHolder, position: Int) {
        val item: NoteListItem = listItems[position]
        val itemUserIndex = startIndex + position
        viewHolder.openButton.text =
            itemUserIndex.toString()

        viewHolder.openButton.contentDescription =
            context.getString(R.string.select_note_command, itemUserIndex)

        viewHolder.showAttachmentButton.text = if (item.isDocument) context.getString(R.string.show_attachment_command, itemUserIndex) else ""
        viewHolder.showAttachmentButton.contentDescription = viewHolder.showAttachmentButton.text


        dateFormat.timeZone = TimeZone.getDefault()
        viewHolder.noteTextView.text = if(item.subject.isNullOrEmpty()) "------------------" else item.subject
        viewHolder.noteOwnerTextView.text = item.owner
        viewHolder.noteDateTextView.text = dateFormat.format(item.date)
        viewHolder.attachmentImageView.visibility = if (item.isDocument) View.VISIBLE else View.INVISIBLE

        disposeBag += viewHolder.openButton.clicks().subscribe {
            clickSubject.onNext(item.id)
        }

        disposeBag += viewHolder.showAttachmentButton.clicks().subscribe {
            showAttachmentSubject.onNext(item.id)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun dispose() {
        disposeBag.dispose()
    }

    override fun isDisposed(): Boolean {
        return disposeBag.isDisposed
    }
}