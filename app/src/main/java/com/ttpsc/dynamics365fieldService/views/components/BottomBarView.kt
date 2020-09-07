package com.ttpsc.dynamics365fieldService.views.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.helpers.extensions.plusAssign
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_bottom_bar.view.*

class BottomBarView : FrameLayout {
    private lateinit var _disposeBag: CompositeDisposable
    private val _goToWorkOrdersSubject = PublishSubject.create<Unit>()
    private val _goToMainMenuSubject = PublishSubject.create<Unit>()
    private val _errorSubject = PublishSubject.create<String>()
    private var _viewVisible: Boolean
        get() = moreOptions.visibility == View.VISIBLE
        set(value) {
            moreOptionsButton.visibility = if (value) View.GONE else View.VISIBLE
            moreOptions.visibility = if (value) View.VISIBLE else View.INVISIBLE
            topBarOverlay.visibility = moreOptions.visibility
            moreOptionsCloseButton.visibility = moreOptions.visibility
        }

    var iconicsLink: String? = null

    fun goToWorkOrdersRequested() = _goToWorkOrdersSubject
    fun goToMainMenuRequested() = _goToMainMenuSubject
    fun errorRequested() = _errorSubject

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

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_bottom_bar, this, true)

        _disposeBag = CompositeDisposable()

        _disposeBag += moreOptionsButton.clicks().subscribe {
            _viewVisible = true
        }

        _disposeBag += moreOptionsCloseButton.clicks().subscribe {
            _viewVisible = false
        }

        _disposeBag += goToWorkOrdersButton.clicks().subscribe {
            _goToWorkOrdersSubject.onNext(Unit)
        }

        _disposeBag += goToMainMenuButton.clicks().subscribe {
            _goToMainMenuSubject.onNext(Unit)
        }

        _disposeBag += goToIconicsButton.clicks().subscribe {
            if (iconicsLink != null) {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(iconicsLink)
                    try {
                        context.startActivity(this)
                    } catch (e: Exception) {
                        if (e.message != null)
                            _errorSubject.onNext(e.message)
                    }
                }
            }
            else {
                try {
                    context.startActivity(context.packageManager.getLaunchIntentForPackage("com.iconics.AndroidAppHub"))
                } catch (e: Exception) {
                    if (e.message != null)
                        _errorSubject.onNext(e.message)
                }
            }
        }
    }

    fun setMoreOptionsButtonVisible(visible : Boolean) {
        moreOptionsButton.visibility = if (visible) View.VISIBLE else View.GONE
    }
}