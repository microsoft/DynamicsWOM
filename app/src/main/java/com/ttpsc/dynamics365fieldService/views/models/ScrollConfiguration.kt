package com.ttpsc.dynamics365fieldService.views.models

import com.ttpsc.dynamics365fieldService.views.enums.ScrollOrientation

data class ScrollConfiguration(
    val orientation: ScrollOrientation,
    val scrollSpeed: Float, // dimensional pixels per degree of rotation
    val autoScrollSpeed: Float, // dimensional pixels per second
    val autoScrollThreshold: Float // degrees
)