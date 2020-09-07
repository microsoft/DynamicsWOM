package com.ttpsc.dynamics365fieldService.views.components

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.ttpsc.dynamics365fieldService.R

class VerticalItemViewHolder(simpleItemView: View) : RecyclerView.ViewHolder(simpleItemView) {
    var indexButton: Button = simpleItemView.findViewById(R.id.itemIndexButton)
    var titleButton: Button = simpleItemView.findViewById(R.id.itemTextButton)
}