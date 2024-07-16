package com.ctg.filetrace

import com.ctg.filetrace.util.DesEncryptor
import com.ctg.filetrace.util.Logger

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/21 18:06
 */
object FileTraceApi {
    internal const val SUCCESS = "success"

    private val encryptor by lazy { DesEncryptor() }

    /**
     * 在非UI线程调用
     * @param filePath 文件路径
     * @param tag 标签信息
     * @return 是否成功
     */
    fun setTag(filePath: String, tag: String): Boolean {
        val encryptedTag = encryptor.encrypt(tag)
        if (null == encryptedTag) {
            Logger.e("fail to encrypt tag: $tag")
            return false
        }

        val accessor = TagAccessorFactory.create(filePath)
        if (null == accessor) {
            Logger.e("不支持该文件类型 $filePath")
        }
        val result = accessor?.setTag(filePath, encryptedTag).orEmpty()
        Logger.d(result)
        return result.startsWith(SUCCESS)
    }

    /**
     * 在非UI线程调用
     * @param filePath 文件路径
     * @return tag 标签信息
     */
    fun getTag(filePath: String): String {
        val accessor = TagAccessorFactory.create(filePath)
        if (null == accessor) {
            Logger.e("不支持该文件类型 $filePath")
        }
        return accessor?.getTag(filePath)?.let { encryptedTag ->
            try {
                encryptor.decrypt(encryptedTag).also { tag ->
                    Logger.d("tag=$tag tagBeforeDecrypt=$encryptedTag filePath=$filePath")
                }
            } catch (thr: Throwable) {
                Logger.e("decrypt fail tagBeforeDecrypt=$encryptedTag", thr)
                null
            }
        }.orEmpty()
    }

    /**
     * @param enableDebug 开启打印debug信息
     */
    fun setDebug(enableDebug: Boolean) {
        Logger.setLogEnable(enableDebug)
        FileTrace().setDebug(enableDebug)
    }

    /**
     * @param url 文件路径
     * @return 是否是支持的文件类型
     */
    fun isSupportFileType(url: String): Boolean {
        return TagAccessorFactory.create(url, false) != null
    }
}