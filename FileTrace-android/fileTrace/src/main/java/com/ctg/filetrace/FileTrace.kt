package com.ctg.filetrace

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/21 18:06
 */
internal class FileTrace : TagAccessor {

    external override fun setTag(filePath: String, tag: String): String
    external override fun getTag(filePath: String): String
    external override fun setDebug(enableDebug: Boolean)

    // Used to load the 'native-lib' library on application startup.
    init {
        System.loadLibrary("ctgfiletrace-lib")
    }
}