package com.ttpsc.dynamics365fieldService.views.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getTextOrThrow
import com.ttpsc.dynamics365fieldService.R
import kotlinx.android.synthetic.main.view_top_bar.view.*

class TopBarView : ConstraintLayout {
    var topBarTitle: String
        get() = viewTitle.text.toString()
        set(value) {
            viewTitle.text = value
        }

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
        LayoutInflater.from(context).inflate(R.layout.view_top_bar, this, true)

        if (attributes != null) {
            context.theme.obtainStyledAttributes(
                attributes,
                R.styleable.TopBarView,
                0,
                0
            ).apply {
                try {
                    topBarTitle = getTextOrThrow(R.styleable.TopBarView_topBarTitle).toString()
                } catch (_: Exception) {
                    // ignored
                } finally {
                    recycle()
                }
            }
        }
    }
}