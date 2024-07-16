package com.ctg.filetrace

import com.ctg.filetrace.impl.OfficeXImpl
import com.ctg.filetrace.impl.ZipImpl
import com.ctg.filetrace.util.suffix
import java.io.File

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/24 09:53
 */
object TagAccessorFactory {
    fun create(filePath: String, checkFileExist: Boolean = true): TagAccessor? {
        return File(filePath).takeIf {
            checkFileExist.not() || (it.exists() && it.isDirectory.not())
        }?.let {
            when (SupportFileType.fromSuffix(it.suffix.toLowerCase())) {
                SupportFileType.PDF -> FileTrace()
                SupportFileType.ZIP -> ZipImpl()
                SupportFileType.OFFICE_X -> OfficeXImpl()
                else -> null
            }
        }
    }
}