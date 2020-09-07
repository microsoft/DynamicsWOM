package com.ttpsc.dynamics365fieldService.views.components

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.R

class GuidesListItemViewHolder(guidesListItemView: View) : RecyclerView.ViewHolder(guidesListItemView) {
    var button: Button = guidesListItemView.findViewById(R.id.button)
    var title: TextView = guidesListItemView.findViewById(R.id.title)
    var status: TextView = guidesListItemView.findViewById(R.id.status)
    var workOrder: TextView = guidesListItemView.findViewById(R.id.workOrder)
    var endTime: TextView = guidesListItemView.findViewById(R.id.endTime)
}