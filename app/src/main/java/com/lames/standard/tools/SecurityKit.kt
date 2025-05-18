@file:JvmName("SecurityKit")

package com.lames.standard.tools

import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec


/**
 * 将字符串加密成 MD5 字符串
 */
fun String.toMD5(charset: Charset = Charsets.UTF_8): String {
    val bmd5 = MessageDigest.getInstance("MD5")
    bmd5.update(this.toByteArray())
    val buf = StringBuffer()
    return toHexString(bmd5.digest(buf.toString().toByteArray(charset)))
}

/**
 * 以 DES 方式加密字符串
 */
fun String.desEncrypt(key: String, charset: Charset = Charsets.UTF_8): String {
    val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
    val desKeySpec = DESKeySpec(key.toByteArray(charset))
    val keyFactory = SecretKeyFactory.getInstance("DES")
    val secretKey = keyFactory.generateSecret(desKeySpec)
    val iv = IvParameterSpec(key.toByteArray(charset))
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
    return toHexString(cipher.doFinal(this.toByteArray(charset)))
}

/**
 * 以 DES 方式解密字符串
 */
fun String.desDecrypt(key: String, charset: Charset = Charsets.UTF_8): String {
    val byteSrc = convertHexString(this)
    val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
    val desKeySpec = DESKeySpec(key.toByteArray(charset))
    val keyFactory = SecretKeyFactory.getInstance("DES")
    val secretKey = keyFactory.generateSecret(desKeySpec)
    val iv = IvParameterSpec(key.toByteArray(charset))
    cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
    return String(cipher.doFinal(byteSrc))
}

/**
 * 将字符串转字节数组
 */
fun convertHexString(content: String): ByteArray {
    val digest = ByteArray(content.length / 2)
    for (i in digest.indices) {
        val byteString = content.substring(2 * i, 2 * i + 2)
        val byteValue = Integer.parseInt(byteString, 16)
        digest[i] = byteValue.toByte()
    }
    return digest
}

/**
 * 将字节数组转换成字符串
 */
fun toHexString(array: ByteArray): String {
    val hexString = StringBuilder()
    for (i in array.indices) {
        var plainText = Integer.toHexString(0xff and array[i].toInt())
        if (plainText.length < 2) plainText = "0$plainText"
        hexString.append(plainText)
    }
    return hexString.toString().toUpperCase(Locale.ENGLISH)
}

/**
 * 获取内容的sha1
 */
@Throws(Exception::class)
fun String.toSh1String(charset: Charset = Charsets.UTF_8): String {
    return MessageDigest.getInstance("SHA1").digest(this.toByteArray(charset))
        .fold("", { str, it -> str + "%02x".format(it) })
}

/**
 * 获取内容的sha256
 */
@Throws(Exception::class)
fun String.toSh256String(charset: Charset = Charsets.UTF_8): String {
    return MessageDigest.getInstance("SHA-256").digest(this.toByteArray(charset))
        .fold("", { str, it -> str + "%02x".format(it) })
}