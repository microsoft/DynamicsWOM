package com.ttpsc.dynamics365fieldService.views.adapters

import android.app.AlertDialog
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.views.components.GuidesListItem
import com.ttpsc.dynamics365fieldService.views.components.GuidesListItemViewHolder
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class GuidesListAdapter() :
    RecyclerView.Adapter<GuidesListItemViewHolder?>(), Disposable {

    private val subject = PublishSubject.create<String>()
    private val disposeBag = CompositeDisposable()
    private var startIndex = 1
    private lateinit var context: Context
    private val listItems: MutableList<GuidesListItem> = mutableListOf()
    private var dateFormat =  SimpleDateFormat("dd MMM yyyy h:mm a", Locale.US)

    fun onItemClick(): Observable<String> {
        return subject
    }

    fun updateData(categories: List<GuidesListItem>, startIndex: Int) {
        this.startIndex = startIndex
        listItems.clear()
        listItems.addAll(categories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuidesListItemViewHolder {
        context = parent.context

        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.guides_list_item, parent, false)

        return GuidesListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: GuidesListItemViewHolder, position: Int) {
        val item: GuidesListItem = listItems[position]
        val itemUserIndex = startIndex + position

        viewHolder.button.text = itemUserIndex.toString()
        viewHolder.button.contentDescription =
            context.getString(R.string.select_booking_command, itemUserIndex)

        viewHolder.title.text = item.title
        viewHolder.status.setText(Html.fromHtml(
            context.getString(R.string.guide_status, item.status),
            Html.FROM_HTML_MODE_LEGACY
        ))
        viewHolder.workOrder.text = context.getString(R.string.guide_work_order, item.workOrder ?: "")
        viewHolder.endTime.text = context.getString(R.string.guide_end_time, item.endTime?.let {
            dateFormat.format(it)
        } ?: "")

        disposeBag += viewHolder.button.clicks().subscribe {
            if (item.workOrder != null) {
                subject.onNext(item.itemId)
                return@subscribe
            }

            AlertDialog.Builder(context).apply {
                setTitle(android.R.string.dialog_alert_title)
                setMessage(context.getString(R.string.cant_open_booking_no_work_order, itemUserIndex))
                setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            }.create().show()
        }

        // TODO For future use if anyone wants status to colors mapping
        /*
        viewHolder.button.background = ContextCompat.getDrawable(
            viewHolder.button.context,
            when (item.listItemStatus) {
                ListItemStatus.NONE ->
                    R.drawable.normal_list_button_background
                ListItemStatus.IN_PROGRESS ->
                    R.drawable.in_progress_list_button_background
                ListItemStatus.DONE ->
                    R.drawable.done_list_button_background
            }
        )
        */
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