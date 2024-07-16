package com.ctg.filetrace.impl

import com.ctg.filetrace.FileTraceApi
import com.ctg.filetrace.TagAccessor
import com.ctg.filetrace.util.FileUtils
import com.ctg.filetrace.util.Logger
import com.ctg.filetrace.util.unzip
import java.io.File

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/24 09:50
 */
open class ZipImpl : TagAccessor {
    override fun setTag(filePath: String, tag: String): String {
        val srcZipFile = File(filePath)
        val unzipDir = getUnZipDir(srcZipFile, false)
        FileUtils.deleteFile(unzipDir)
        srcZipFile.unzip(unzipDir)

        val xmlResult = setToXml(unzipDir, tag)
        if (xmlResult.not()) {
            return "writeString to xmlFile fail"
        }

        val tagFile = File(unzipDir.absolutePath + File.separator + TAG_FILE_PATH)
        var ret = FileUtils.writeString(tagFile, tag, false)
        if (ret.not()) {
            return "writeString to tagFile fail"
        }

        val destFile = File(unzipDir.absolutePath + File.separator + TMP_ZIP_SUFFIX)
        ret = FileUtils.zip(unzipDir.listFiles(), destFile)
        if (ret.not()) {
            return "reZip files fail"
        }

        FileUtils.deleteFile(srcZipFile)
        destFile.renameTo(srcZipFile)
        FileUtils.deleteFile(unzipDir)
        return FileTraceApi.SUCCESS
    }

    override fun getTag(filePath: String): String {
        val srcZipFile = File(filePath)
        val unzipDir = getUnZipDir(srcZipFile, true)
        FileUtils.deleteFile(unzipDir)
        srcZipFile.unzip(unzipDir)

        val tagFromXml = readFromXml(unzipDir).orEmpty()
        if (tagFromXml.isNotBlank()) {
            Logger.d("OfficeXImpl: getTag from xml")
            FileUtils.deleteFile(unzipDir)
            return tagFromXml
        }

        val tagFile = File(unzipDir.absolutePath + File.separator + TAG_FILE_PATH)
        return FileUtils.readString(tagFile.absolutePath).orEmpty().also {
            FileUtils.deleteFile(unzipDir)
        }
    }

    override fun setDebug(enableDebug: Boolean) {
        // do nothing
    }

    protected open fun setToXml(unzipDir: File, tag: String): Boolean {
        // do nothing
        return true
    }

    protected open fun readFromXml(unzipDir: File): String? = null

    companion object {
        private const val TAG_FILE_PATH = "docProps/ctgFTag"
        private const val TMP_ZIP_SUFFIX = "ctg.zip"
        private const val TMP_SUFFIX = "-ctgTmp"

        /**
         * @param rw true->读  false->写
         */
        private fun getUnZipDir(srcZipFile: File, rw: Boolean): File {
            val suffix = if (rw) "r" else "w"
            return File(srcZipFile.parentFile.absolutePath + File.separator + srcZipFile.nameWithoutExtension + TMP_SUFFIX + suffix)
        }
    }
}