package com.lames.standard.camera

import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.animation.DecelerateInterpolator
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityMedCameraBinding
import com.lames.standard.dialog.AlertDialogFragment
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class MedCameraActivity : CommonActivity<ActivityMedCameraBinding>() {

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null
    private val cameraExecutor by lazy { Executors.newSingleThreadExecutor() }

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private val oreListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == -1) return

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }

        }
    }

    override fun getViewBinding() = ActivityMedCameraBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        val permissions = arrayListOf(
            Permission.READ_MEDIA_IMAGES,
            Permission.CAMERA,
            Permission.WRITE_EXTERNAL_STORAGE
        )
        if (XXPermissions.isGranted(this, permissions)) startCamera()
        else AlertDialogFragment().also {
            it.title = forString(R.string.alert_tip_title)
            it.content = forString(R.string.desc_request_camera_audio_permission)
            it.onConfirm = {
                XXPermissions.with(this).unchecked().permission(permissions)
                    .request { _, allGranted ->
                        if (allGranted.not()) {
                            showErrorToast(R.string.lack_of_needed_permission)
                            finish()
                        } else startCamera()
                    }
            }
        }.show(supportFragmentManager, null)
    }

    override fun bindEvent() {
        oreListener.enable()
        binding.close.onClick { onBackPressedDispatcher.onBackPressed() }

        binding.switchCamera.onClick {
            startCamera(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
        }

        binding.takePhoto.onClick {
            takePhoto()
        }

    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val fileName = SimpleDateFormat(
            "yyyy_MM_dd_HH_mm_ss_SSS",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Images.Media.TITLE, fileName)
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.DESCRIPTION, "")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                    put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                }).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    LogKit.e(javaClass.simpleName, "Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    LogKit.d(javaClass.simpleName, "take Photo success")
                    val uri = output.savedUri ?: return
                    setResult(
                        RESULT_OK,
                        Intent().also { it.putExtra(Constants.Params.ARG1, uri.toString()) })
                    finish()
                }
            })
        floatAnimate(0L, 400L, 0f, 1f, 1f, 0f) { binding.flashCover.alpha = it }
    }


    private fun startCamera(isFront: Boolean = false) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()
            videoCapture = VideoCapture.Builder().build()
            cameraSelector =
                if (isFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture
                )
                //cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (exc: Exception) {
                LogKit.e(javaClass.simpleName, "$exc")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        oreListener.disable()
    }

    private inline fun floatAnimate(
        startDelay: Long,
        duration: Long,
        vararg values: Float,
        crossinline block: (Float) -> Unit
    ) {
        ValueAnimator.ofFloat(*values).apply {
            setStartDelay(startDelay)
            setDuration(duration)
            interpolator = DecelerateInterpolator()
            addUpdateListener { block(it.animatedValue as Float) }
        }.start()
    }

}