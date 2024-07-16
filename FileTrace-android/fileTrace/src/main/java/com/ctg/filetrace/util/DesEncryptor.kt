package com.ctg.filetrace.util

import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/24 11:26
 */
class DesEncryptor {

    @Throws(
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        NoSuchPaddingException::class,
        UnsupportedEncodingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(src: String): String? {
        if (src.isEmpty()) return src

        val desKey = DESKeySpec(DES_KEY.toByteArray())
        val keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM)
        val securekey = keyFactory.generateSecret(desKey)
        val cipher = Cipher.getInstance(DES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, securekey)
        val decoder = Base64.getDecoder()
        val decodeBase64 = decoder.decode(src)
        val finalData = String(decodeBase64, charset(UTF8))
        var encryptedData = decodeBase64
        var version = -1
        finalData.indexOfFirst { it == PLUS }.takeIf { it > -1 }?.let { index ->
            version = finalData.substring(1, index).toIntOrNull() ?: -1
            val presize = finalData.substring(0, index + 1).toByteArray().size
            val size = decodeBase64.size - presize
            encryptedData = ByteArray(size)
            System.arraycopy(decodeBase64, presize, encryptedData, 0, size)
            encryptedData = decoder.decode(encryptedData)
            Logger.d("decrypt version = $version")
        }
        return String(cipher.doFinal(encryptedData), charset(UTF8))
    }

    @Throws(
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        InvalidKeySpecException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        UnsupportedEncodingException::class
    )
    fun encrypt(data: String): String? {
        val desKey = DESKeySpec(DES_KEY.toByteArray())
        val keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM)
        val securekey = keyFactory.generateSecret(desKey)
        val cipher = Cipher.getInstance(DES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, securekey)
        val encryptedData = cipher.doFinal(data.toByteArray(charset(UTF8)))
        val encoder = Base64.getEncoder()
        val tmpData = encoder.encodeToString(encryptedData)
        return encoder.encodeToString("$E_VERSION_PREFIX$tmpData".toByteArray())
    }

    companion object {
        private const val UTF8: String = "utf-8"
        private const val DES_ALGORITHM: String = "DES"
        private const val DES_KEY: String = "5NDZOADK"

        private const val ENCRYPT_VERSION = 1
        private const val PLUS = '+'
        private const val E_VERSION_PREFIX = "v${ENCRYPT_VERSION}$PLUS"
    }
}