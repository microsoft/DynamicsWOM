package com.ttpsc.dynamics365fieldService.helpers.observables

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import com.ttpsc.dynamics365fieldService.helpers.models.GyroscopeData
import com.ttpsc.dynamics365fieldService.helpers.models.Vector3
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.time.Duration
import java.time.Instant

class GyroscopeSensor(private val context: Context) : Observable<GyroscopeData>() {
    override fun subscribeActual(observer: Observer<in GyroscopeData>?) {
        var cumulativeRotation = Vector3()
        var lastMeasurement = Instant.now()

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gyroscopeSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val gyroscopeSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                val timeMeasurement = Instant.now()
                val measurement = Vector3(
                    if (windowManager.defaultDisplay.rotation == Surface.ROTATION_180) {
                        -sensorEvent.values[0]
                    } else {
                        sensorEvent.values[0]
                    },
                    if (windowManager.defaultDisplay.rotation == Surface.ROTATION_180) {
                        -sensorEvent.values[1]
                    } else {
                        sensorEvent.values[1]
                    },
                    sensorEvent.values[2]
                ) * Vector3(4f, 4f, 4f)
                cumulativeRotation += measurement
                observer?.onNext(
                    GyroscopeData(
                        measurement,
                        cumulativeRotation,
                        timeDelta = Duration.between(lastMeasurement, timeMeasurement)
                    )
                )
                lastMeasurement = timeMeasurement
            }

            override fun onAccuracyChanged(sensor: Sensor?, i: Int) {}
        }
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        observer?.onSubscribe(Disposable.fromAction {
            sensorManager.unregisterListener(gyroscopeSensorListener)
        })
    }
}