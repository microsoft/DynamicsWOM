package com.ttpsc.dynamics365fieldService.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.views.components.VerticalItemViewHolder
import com.ttpsc.dynamics365fieldService.views.components.VerticalListItem
import com.ttpsc.dynamics365fieldService.views.configuration.ViewConfig
import com.ttpsc.dynamics365fieldService.views.enums.ListItemStatus
import com.ttpsc.dynamics365fieldService.views.extensions.addGyroscopeScroll
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

class VerticalListAdapter(private var listItems: MutableList<VerticalListItem>) :
    RecyclerView.Adapter<VerticalItemViewHolder?>(), Disposable {

    private val subject = PublishSubject.create<String>()
    private val disposeBag = CompositeDisposable()
    private lateinit var context: Context

    fun onItemClick(): Observable<String> {
        return subject
    }

    fun updateData(categories: List<VerticalListItem>) {
        listItems.clear()
        listItems.addAll(categories)
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        disposeBag += recyclerView.addGyroscopeScroll(ViewConfig.verticalListConfiguration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalItemViewHolder {
        context = parent.context

        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.simple_item_layout, parent, false)

        return VerticalItemViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: VerticalItemViewHolder, position: Int) {
        val item: VerticalListItem = listItems[position]

        val indexButton = viewHolder.indexButton
        viewHolder.indexButton.text =
            (position + 1).toString()

        viewHolder.indexButton.contentDescription =
            context.getString(R.string.select_item_command) + (position + 1).toString()

        val titleButton = viewHolder.titleButton
        titleButton.text = item.title

        disposeBag += indexButton.clicks().subscribe {
            subject.onNext(item.key)
        }

        disposeBag += titleButton.clicks().subscribe {
            subject.onNext(item.key)
        }
        indexButton.background = ContextCompat.getDrawable(
            indexButton.context,
            when (item.status) {
                ListItemStatus.NONE ->
                    R.drawable.normal_list_button_background
                ListItemStatus.DONE ->
                    R.drawable.done_list_button_background
                ListItemStatus.IN_PROGRESS ->
                    R.drawable.in_progress_list_button_background
            }
        )
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