package com.ttpsc.dynamics365fieldService.views.configuration

import com.ttpsc.dynamics365fieldService.views.enums.ScrollOrientation
import com.ttpsc.dynamics365fieldService.views.models.ScrollConfiguration

object ViewConfig {
    val categoryListConfiguration = ScrollConfiguration(ScrollOrientation.Horizontal, 10f, 50f, 30f)
    val verticalListConfiguration = ScrollConfiguration(ScrollOrientation.Vertical, 10f, 50f, 30f)
}