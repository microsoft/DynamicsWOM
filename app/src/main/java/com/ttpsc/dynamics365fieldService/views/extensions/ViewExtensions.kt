package com.ttpsc.dynamics365fieldService.views.extensions

import android.view.View
import com.ttpsc.dynamics365fieldService.helpers.observables.GyroscopeSensor
import com.ttpsc.dynamics365fieldService.views.enums.ScrollOrientation
import com.ttpsc.dynamics365fieldService.views.models.ScrollConfiguration
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.math.abs

@CheckReturnValue
fun View.addGyroscopeScroll(configuration: ScrollConfiguration): Disposable {
    return GyroscopeSensor(this.context).subscribe {
        val cumulativeRotation = when (configuration.orientation) {
            ScrollOrientation.Horizontal -> it.cumulativeRotation.y
            ScrollOrientation.Vertical -> it.cumulativeRotation.x
        }
        val scrollPixels: Int
        @Suppress("LiftReturnOrAssignment")
        if (abs(cumulativeRotation) > configuration.autoScrollThreshold) {
            val scrollDistance = configuration.autoScrollSpeed * it.timeDelta.toMillis() / 100f
            scrollPixels = if (cumulativeRotation > 0f) {
                -scrollDistance.toInt()
            } else {
                scrollDistance.toInt()
            }
        } else {
            val scrollDistance = -configuration.scrollSpeed * when (configuration.orientation) {
                ScrollOrientation.Horizontal -> it.lastRotation.y
                ScrollOrientation.Vertical -> it.lastRotation.x
            }
            scrollPixels = scrollDistance.toInt()
        }
        when (configuration.orientation) {
            ScrollOrientation.Horizontal -> this.scrollBy(scrollPixels, 0)
            ScrollOrientation.Vertical -> this.scrollBy(0, scrollPixels)
        }
    }
}