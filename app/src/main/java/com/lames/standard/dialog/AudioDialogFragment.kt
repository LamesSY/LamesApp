package com.lames.standard.dialog

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Base64
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lames.standard.R
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.databinding.DialogFragmentAudioBinding
import com.lames.standard.image.ImageKit
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.network.Api
import com.lames.standard.network.launchX
import com.lames.standard.network.postRequest
import com.lames.standard.tools.AudioRecordTool
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick
import com.lames.standard.tools.safeCancel
import com.lames.standard.tools.showDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import rxhttp.tryAwait
import rxhttp.wrapper.param.toAwaitResultData
import java.io.File

class AudioDialogFragment : CommonDialogFragment<DialogFragmentAudioBinding>() {

    /**
     * @param code -1无需发送  1发送base64 2发送文字
     */
    var onAudioInputEnd: ((code: Int, pcmBase64: String, audioText: String) -> Unit)? = null

    private var audioTimeJob: Job? = null
    private var audioResultText: String = EMPTY_STR

    override fun dialogType(): Int = 1

    override fun getViewBinding(inflater: LayoutInflater) =
        DialogFragmentAudioBinding.inflate(inflater)

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        isCancelable = false
    }

    override fun initialization() {
        ImageKit.with(this).asGif().load(R.mipmap.gif_audio_recording).into(binding.recordingGif)
        val permissions = listOf(Permission.RECORD_AUDIO)
        val isGant = XXPermissions.isGranted(requireContext(), *permissions.toTypedArray())
        if (isGant.not()) showDialogFragment<AlertDialogFragment>(parentFragmentManager) {
            it.title = forString(R.string.alert_tip_title)
            it.content = forString(R.string.request_permission_for_audio_record)
            it.onConfirm = {
                XXPermissions.with(this).permission(permissions).request { _, allGranted ->
                    if (allGranted.not()) dismiss()
                }
            }
            it.onCancel = { dismiss() }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bindEvent() {
        binding.audioDialogBg.onClick { dismiss() }

        binding.cancelSend.onClick {
            //onAudioInputEnd?.invoke(-1, EMPTY_STR, EMPTY_STR)
            dismiss()
        }

        binding.sendText.onClick {
            val pcmFile = File(AudioRecordTool.audioPath)
            val base64 = Base64.encodeToString(pcmFile.readBytes(), Base64.NO_WRAP)
            onAudioInputEnd?.invoke(2, base64, audioResultText)
            dismiss()
        }

        binding.sendVoice.onClick {
            val pcmFile = File(AudioRecordTool.audioPath)
            val base64 = Base64.encodeToString(pcmFile.readBytes(), Base64.NO_WRAP)
            onAudioInputEnd?.invoke(1, base64, EMPTY_STR)
            dismiss()
        }

        binding.btnAudio.setOnLongClickListener {
            binding.close.isVisible = true
            binding.transText.isVisible = true
            binding.contentLayout.isVisible = true
            binding.btnAudio.setImageResource(R.mipmap.img_audio_ing)
            binding.actionTip.setText(R.string.loose_to_send)
            AudioRecordTool.startRecording()
            startAudioTimeJob()
            binding.audioDialogBg.onClick { }
            return@setOnLongClickListener true
        }

        binding.btnAudio.setOnTouchListener { v, event ->
            if (event.action != MotionEvent.ACTION_UP && event.action != MotionEvent.ACTION_CANCEL) return@setOnTouchListener false
            if (AudioRecordTool.isRecording().not()) return@setOnTouchListener false
            AudioRecordTool.stopRecording()
            if (event.isInView(binding.close)) {
                //onAudioInputEnd?.invoke(-1, EMPTY_STR, EMPTY_STR)
                dismiss()
                return@setOnTouchListener false
            }
            if (event.isInView(binding.transText)) showAudioInputResult()
            else {
                val pcmFile = File(AudioRecordTool.audioPath)
                val base64 = Base64.encodeToString(pcmFile.readBytes(), Base64.NO_WRAP)
                onAudioInputEnd?.invoke(1, base64, EMPTY_STR)
                dismiss()
            }
            v.performClick()
            return@setOnTouchListener false
        }
    }

    private fun showAudioInputResult() {
        binding.btnAudio.isInvisible = true
        binding.close.isInvisible = true
        binding.transText.isInvisible = true
        binding.time.isInvisible = true
        binding.actionTip.isInvisible = true
        binding.recordingGif.isInvisible = true
        binding.audioText.isVisible = true
        binding.audioText.setText(R.string.voice_being_recognized)
        val pcmFile = File(AudioRecordTool.audioPath)
        val base64 = Base64.encodeToString(pcmFile.readBytes(), Base64.NO_WRAP)
        lifecycleScope.launchX {
            val params = hashMapOf<String, Any?>()
            params["fileName"] = "audio_record"
            params["uid"] = UserMMKV.user?.uid
            params["voiceBase64Str"] = base64
            val content =
                postRequest(Api.Audio.TO_TEXT, params).toAwaitResultData<String>().tryAwait()
                    ?: EMPTY_STR
            binding.sendLayout.isVisible = true
            audioResultText = content
            binding.audioText.text = content.ifEmpty { forString(R.string.not_find_audio_content) }
        }
    }

    private fun startAudioTimeJob() {
        audioTimeJob?.safeCancel()
        audioTimeJob = lifecycleScope.launchX {
            repeat(30) {
                binding.time.text = "00:%02d".format(it)
                delay(1000)
            }
            if (AudioRecordTool.isRecording().not()) return@launchX
            AudioRecordTool.stopRecording()
            showAudioInputResult()
        }
    }

    private fun MotionEvent.isInView(v: View): Boolean {
        val location = IntArray(2).also { v.getLocationOnScreen(it) }
        val flagX = this.rawX >= location[0] && this.rawX <= location[0] + v.width
        val flagY = this.rawY >= location[1] && this.rawY <= location[1] + v.height
        return flagX && flagY
    }

    private fun MotionEvent.isInRect(rect: Rect): Boolean =
        rect.contains(this.rawX.toInt(), this.rawY.toInt())

    override fun onDestroyView() {
        if (AudioRecordTool.isRecording()) AudioRecordTool.stopRecording()
        super.onDestroyView()
    }

}