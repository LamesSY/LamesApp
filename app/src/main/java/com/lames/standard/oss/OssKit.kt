package com.lames.standard.oss

import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.model.ObjectMetadata
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.lames.standard.common.CommonApp
import com.lames.standard.common.Constants
import com.lames.standard.common.Constants.Oss.BUCKET
import com.lames.standard.entity.OssToken
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.network.Api
import com.lames.standard.network.getData
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Jason
 */
object OssKit {

    val TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.CHINA)
    private var writeClient: OSSClient? = null
    private var writeToken: OssToken? = null

    private suspend fun updateWriteToken() {
        val token = getData<OssToken>(Api.Oss.GET_WRITE_TOKEN, hashMapOf())
        writeToken = token
        writeClient = OSSClient(
            CommonApp.obtain(),
            Constants.Oss.ENDPOINT,
            OSSStsTokenCredentialProvider(
                token.accessKeyId,
                token.accessKeySecret,
                token.securityToken
            )
        )
    }

    suspend fun uploadFile(
        folder: String,
        fileName: String,
        localPath: String,
        progressBlock: ((Int) -> Unit)?
    ): String? {
        updateWriteToken()
        if (writeToken == null || writeClient == null) return null
        val config = ClientConfiguration()
        config.connectionTimeout = 15 * 1000
        config.socketTimeout = 15 * 1000
        config.maxConcurrentRequest = 5
        config.maxErrorRetry = 2
        val putRequest = PutObjectRequest(BUCKET, "$folder$fileName", localPath)
        val objMetaData = ObjectMetadata()
        when {
            fileName.endsWith(".mp4", true) -> objMetaData.contentType = "video/mp4"
            fileName.endsWith(".zip", true) -> objMetaData.contentType = "application/zip"
            fileName.endsWith(".txt", true) -> objMetaData.contentType = "text/plain"
            fileName.endsWith(".aac", true) -> objMetaData.contentType = "audio/aac"
            fileName.endsWith(".mp4", true) -> objMetaData.contentType = "video/mp4"
            fileName.endsWith(".png", true) -> objMetaData.contentType = "image/png"
            else -> objMetaData.contentType = "image/jpeg"
        }
        putRequest.metadata = objMetaData
        putRequest.progressCallback = OSSProgressCallback { _, currentSize, totalSize ->
            progressBlock?.invoke((currentSize.toFloat() * 100 / totalSize).toInt())
        }
        writeClient?.putObject(putRequest)
        return "${Constants.Oss.URL_PREFIX}${putRequest.objectKey}"
    }

    suspend fun uploadFiles(
        folder: String,
        fileName: String,
        localPathList: List<String>,
        progressBlock: ((Int) -> Unit)?
    ): List<String>? {
        updateWriteToken()
        if (writeToken == null || writeClient == null) return null
        val config = ClientConfiguration()
        config.connectionTimeout = 15 * 1000
        config.socketTimeout = 15 * 1000
        config.maxConcurrentRequest = 5
        config.maxErrorRetry = 2
        val objMetaData = ObjectMetadata()
        val objectKeys = mutableListOf<String>()
        when {
            fileName.endsWith(".mp4", true) -> objMetaData.contentType = "video/mp4"
            fileName.endsWith(".zip", true) -> objMetaData.contentType = "application/zip"
            fileName.endsWith(".txt", true) -> objMetaData.contentType = "text/plain"
            fileName.endsWith(".aac", true) -> objMetaData.contentType = "audio/aac"
            fileName.endsWith(".mp4", true) -> objMetaData.contentType = "video/mp4"
            fileName.endsWith(".png", true) -> objMetaData.contentType = "image/png"
            else -> objMetaData.contentType = "image/jpeg"
        }
        localPathList.forEachIndexed { index, localPath ->
            val newFileName = fileName.replace(".", "_${index}.")
            val putRequest = PutObjectRequest(BUCKET, "$folder$newFileName", localPath)
            putRequest.metadata = objMetaData
            putRequest.progressCallback = OSSProgressCallback { _, currentSize, totalSize ->
                progressBlock?.invoke((currentSize.toFloat() * 100 / totalSize).toInt())
            }
            writeClient?.putObject(putRequest)
            objectKeys.add(putRequest.objectKey)
        }
        return objectKeys
    }


    /**
     * 用户头像路径
     */
    val userAvatarFolder get() = "user_avatar/${UserMMKV.user?.uid}/"

    /**
     * 用户头像文件名
     */
    val userAvatarName get() = "user_avatar_${System.currentTimeMillis()}_${UserMMKV.user?.uid}"

    /**
     * AI报告识别中的报告图片路径
     */
    val aiReportFolder get() = "ai_report/${UserMMKV.user?.uid ?: "unknown uid"}/"

    /**
     * AI报告识别中的报告图片名称
     */
    val aiReportName get() = "ai_report_${UserMMKV.user?.uid ?: "unknown uid"}_${System.currentTimeMillis()}"

}