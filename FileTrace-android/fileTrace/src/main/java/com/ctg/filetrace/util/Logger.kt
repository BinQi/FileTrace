package com.ctg.filetrace.util

import android.util.Log

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/24 09:11
 */
object Logger {

    private const val TAG = "file-trace"
    private var logEnable = false

    fun d(msg: String) {
        d(TAG, msg)
    }
    fun d(tag: String, msg: String) {
        if (logEnable) {
            Log.d(tag, msg)
        }
    }
    fun e(msg: String, error: Throwable? = null) {
        e(TAG, msg, error)
    }
    fun e(tag: String, msg: String, error: Throwable? = null) {
        if (logEnable) {
            Log.e(tag, msg, error)
        }
    }
    fun setLogEnable(enable: Boolean) {
        logEnable = enable
    }
}