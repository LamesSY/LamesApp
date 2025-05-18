package com.lames.standard.camera

import android.content.Intent
import android.view.LayoutInflater
import com.canhub.cropper.CropImageView
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityCropImgBinding
import com.lames.standard.tools.onClick

class CropImgActivity : CommonActivity<ActivityCropImgBinding>(),
    CropImageView.OnCropImageCompleteListener {

    private val picUri by lazy { intent.data }

    override fun getViewBinding() = ActivityCropImgBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        binding.cropImageView.setImageUriAsync(picUri)
        binding.cropImageView.setOnCropImageCompleteListener(this)
    }

    override fun bindEvent() {
        binding.cancelCropping.onClick { finish() }

        binding.rotateImage.onClick { binding.cropImageView.rotateImage(-90) }

        binding.confirmCropping.onClick { binding.cropImageView.croppedImageAsync() }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        val path = result.getUriFilePath(this)
        if (path.isNullOrEmpty()) setResult(RESULT_CANCELED)
        else setResult(RESULT_OK, Intent().also { it.putExtra(Constants.Params.ARG1, path) })
        finish()
    }
}