package com.lames.standard.module.login

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.lames.standard.R
import com.lames.standard.camera.CropImgActivity
import com.lames.standard.camera.MedCameraActivity
import com.lames.standard.common.CommonFragment
import com.lames.standard.common.Constants
import com.lames.standard.databinding.FragmentUserInit1Binding
import com.lames.standard.dialog.AlertDialogFragment
import com.lames.standard.dialog.DateSetDialogFragment
import com.lames.standard.dialog.ListDialogFragment
import com.lames.standard.image.ImageKit
import com.lames.standard.tools.AppKit
import com.lames.standard.tools.forColor
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast
import com.lames.standard.tools.toPatternString
import java.util.Calendar

class UserInit1Fragment : CommonFragment<FragmentUserInit1Binding>() {

    private val cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val path = result.data?.getStringExtra(Constants.Params.ARG1)
                ?: return@registerForActivityResult
            if (result.resultCode != RESULT_OK) return@registerForActivityResult
            (requireActivity() as UserInitActivity).avatarLocalPath = path
            ImageKit.with(this).load(path).into(binding.avatar)
        }
    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.getStringExtra(Constants.Params.ARG1)
                ?: return@registerForActivityResult
            val intent = Intent(requireContext(), CropImgActivity::class.java)
            intent.data = uri.toUri()
            cropLauncher.launch(intent)
        }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            val intent = Intent(requireContext(), CropImgActivity::class.java)
            intent.data = uri ?: return@registerForActivityResult
            cropLauncher.launch(intent)
        }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserInit1Binding {
        return FragmentUserInit1Binding.inflate(inflater, container, false)
    }

    override fun initialization() {
        dispatcher.addCallback {
            showDialogFg<AlertDialogFragment> {
                it.content = forString(R.string.confirm_quit_app)
                it.onConfirm = { AppKit.obtain().quit() }
            }
        }

    }

    override fun bindEvent() {
        binding.avatarLayout.onClick {
            val list = mutableListOf(R.string.take_photo, R.string.gallery)
            ListDialogFragment.show(parentFragmentManager, list, { forString(it) }) { index ->
                if (index == 0) launchCameraWithPermission() else launchMediaWithPermission()
            }
        }

        binding.male.onClick {
            binding.male.isChecked = true
            binding.female.isChecked = false
            binding.male.backgroundTintList =
                ColorStateList.valueOf(forColor(R.color.md_theme_primary))
            binding.female.backgroundTintList = ColorStateList.valueOf(forColor(R.color.windowBg_2))
        }

        binding.female.onClick {
            binding.male.isChecked = false
            binding.female.isChecked = true
            binding.male.backgroundTintList = ColorStateList.valueOf(forColor(R.color.windowBg_2))
            binding.female.backgroundTintList =
                ColorStateList.valueOf(forColor(R.color.md_theme_primary))
        }

        binding.birthDate.onClick {
            val defCal = Calendar.getInstance().also { it.set(1965, 0, 1) }
            showDialogFg<DateSetDialogFragment> {
                it.title = forString(R.string.birth_date)
                it.defaultDate = defCal
                it.onSelectedDate = { date ->
                    binding.birthDate.text = date.toPatternString("yyyy-MM-dd")
                }
            }
        }

        binding.nextStep.onClick {
            if (binding.name.text.isNullOrEmpty()) {
                showErrorToast(R.string.plz_input_name)
                return@onClick
            }
            (requireActivity() as UserInitActivity).tempUser.apply {
                nickName = binding.name.text.toString()
                birthday = binding.birthDate.text.toString()
                sex = if (binding.female.isChecked) 0 else 1
            }
            start<UserInit2Fragment>()
        }
    }

    private fun launchCameraWithPermission() {
        takePhoto.launch(Intent(requireContext(), MedCameraActivity::class.java))
    }

    private fun launchMediaWithPermission() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}