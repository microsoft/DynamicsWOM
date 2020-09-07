package com.ttpsc.dynamics365fieldService.views.components

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.R
import kotlinx.android.synthetic.main.notes_list_item.view.*

class NotesListItemViewHolder(notesListItemView: View) : RecyclerView.ViewHolder(notesListItemView) {
    var openButton: Button = notesListItemView.findViewById(R.id.openButton)
    var showAttachmentButton: Button = notesListItemView.findViewById(R.id.showAttachmentButton)
    var noteTextView: TextView = notesListItemView.findViewById(R.id.noteTextView)
    var noteOwnerTextView: TextView = notesListItemView.findViewById(R.id.noteOwnerTextView)
    var noteDateTextView: TextView = notesListItemView.findViewById(R.id.noteDateTextView)
    var attachmentImageView: ImageView = notesListItemView.findViewById(R.id.attachmentImage)
}