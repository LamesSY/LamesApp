package com.lames.standard.tools

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.lames.standard.common.GlobalVar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

object AudioRecordTool {

    val audioPath = GlobalVar.obtain().audioPath + "audio_record.pcm"

    private var audioRecord: AudioRecord? = null
    private var isRecording = AtomicBoolean(false)
    private var recordThread: Thread? = null
    private var outputStream: FileOutputStream? = null
    private var pcmFile: File? = null

    // 设置默认的音频格式和配置
    private const val SAMPLE_RATE = 16000  // 采样率
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO  // 单声道
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT  // 16-bit PCM
    private const val BUFFER_SIZE = 2048  // 缓冲区大小

    /**
     * 开始录音并保存为 PCM 文件
     */
    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (isRecording.get()) {
            LogKit.v(javaClass.simpleName, "Already recording.")
            return
        }

        // 初始化录音设置
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE || minBufferSize == AudioRecord.ERROR) {
            LogKit.v(javaClass.simpleName, "Invalid buffer size.")
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minBufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            LogKit.v(javaClass.simpleName, "AudioRecord initialization failed.")
            return
        }

        // 设置文件输出路径
        pcmFile = File(audioPath)
        outputStream = FileOutputStream(pcmFile)

        // 启动录音线程
        isRecording.set(true)
        recordThread = Thread {
            val buffer = ByteArray(BUFFER_SIZE)
            audioRecord?.startRecording()

            while (isRecording.get()) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    try {
                        // 将录音数据写入文件
                        outputStream?.write(buffer, 0, read)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            stopRecording()
        }
        recordThread?.start()
    }

    /**
     * 手动停止录音
     */
    fun stopRecording() {
        if (!isRecording.get()) {
            LogKit.v(javaClass.simpleName, "Not recording.")
            return
        }

        isRecording.set(false)
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordThread?.join()
        recordThread = null
        outputStream?.close()
        outputStream = null

        LogKit.v(
            javaClass.simpleName,
            "Recording stopped. Audio saved to: ${pcmFile?.absolutePath}"
        )
    }

    /**
     * 获取录音状态
     * @return true if recording, false otherwise
     */
    fun isRecording(): Boolean {
        return isRecording.get()
    }
}
