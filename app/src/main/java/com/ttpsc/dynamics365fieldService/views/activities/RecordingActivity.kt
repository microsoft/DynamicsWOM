package com.ttpsc.dynamics365fieldService.views.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ttpsc.dynamics365fieldService.R
import com.ttpsc.dynamics365fieldService.databinding.ActivityRecordingBinding
import com.ttpsc.dynamics365fieldService.views.extensions.clicks
import kotlinx.android.synthetic.main.activity_recording.*

class RecordingActivity : AppCompatActivity(), SurfaceHolder.Callback {

    companion object {
        private const val TAG = "RecordingActivity"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        const val EXTRA_OUTPUT = "EXTRA_OUTPUT"

        const val BITRATE_LOW : Int = 700000
        const val BITRATE_MEDIUM : Int = 1400000
        const val BITRATE_HIGH : Int = 2500000
    }

    private lateinit var camera: Camera

    lateinit var previewSurfaceHolder: SurfaceHolder
    private var mediaRecorder: MediaRecorder? = null
    var recording = MutableLiveData<Boolean>().apply { postValue(false) }
    var outputUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRecordingBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_recording)
        binding.lifecycleOwner = this
        binding.data = this

        outputUri = intent.getParcelableExtra<Uri>(EXTRA_OUTPUT)
        if (outputUri == null) {
            finish()
            return
        }

        previewSurfaceHolder = surfaceView.holder.apply {
            addCallback(this@RecordingActivity)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
        surfaceView.visibility = View.INVISIBLE
        layoutButtons.visibility = View.INVISIBLE

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                CAMERA_PERMISSION_REQUEST_CODE);
        else
            onPermissionsGranted()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    onPermissionsGranted()
                    return
                }

                AlertDialog.Builder(this).apply {
                    setCustomTitle(
                        layoutInflater.inflate(R.layout.view_alert_dialog_title, null)
                            .apply {
                                findViewById<TextView>(R.id.title).text =
                                    getString(R.string.permissions_required_title)
                            })
                    setMessage(getString(R.string.permissions_required_message))
                    setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                }.create().apply {
                    setOnDismissListener { finish() }
                    show()
                }
            }
        }
    }

    fun onPermissionsGranted() {
        recording.observe(this, Observer {
            startRecordingButton.visibility = if (it == true) View.GONE else View.VISIBLE
            stopRecordingButton.visibility = if (it == true) View.VISIBLE else View.GONE
        })

        startRecordingButton.clicks().subscribe { startRecording() }
        stopRecordingButton.clicks().subscribe { stopRecording() }

        surfaceView.visibility = View.VISIBLE
        layoutButtons.visibility = View.VISIBLE
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceCreated(p0: SurfaceHolder?) {
        try {
            camera = Camera.open().apply {
//                setDisplayOrientation(180)
                setDisplayOrientation(getProperOrientationRotation())
                setPreviewDisplay(previewSurfaceHolder)
                startPreview()
            }
        } catch (e: Exception) {
            Log.e(TAG, "surfaceCreated() Exception: $e")
            finish()
        }
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        camera.stopPreview()
        camera.release()
    }

    private fun startRecording() {
        try {
            camera.unlock()

            mediaRecorder = MediaRecorder().apply {
                setCamera(camera)
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
                setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P))
                setVideoEncodingBitRate(BITRATE_LOW)
                setOutputFile(contentResolver.openFileDescriptor(outputUri!!, "rw")?.fileDescriptor)
                prepare()
                start()
            }

            recording.postValue(true)

            textTime.base = SystemClock.elapsedRealtime()
            textTime.start()
        } catch (e: Exception) {
            Log.v(TAG, "startRecording() Exception: $e")
            finish()
        }
    }

    private fun stopRecording() {
        textTime.stop()

        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }

        recording.postValue(false)

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun getProperOrientationRotation() : Int {
        val rotation: Int = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
//        if (info.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (Camera.CameraInfo().orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360
//        }
        return result
    }
}
