package com.ttpsc.dynamics365fieldService.helpers.models

import java.time.Duration

class GyroscopeData(
    var lastRotation: Vector3 = Vector3(),
    var cumulativeRotation: Vector3 = Vector3(),
    var timeDelta: Duration = Duration.ZERO
) {

    override fun toString(): String {
        return "last: $lastRotation, cumulative: $cumulativeRotation, timeDelta: $timeDelta"
    }
}