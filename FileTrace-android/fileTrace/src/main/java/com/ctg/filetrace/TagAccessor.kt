package com.ctg.filetrace

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/24 09:46
 */
interface TagAccessor {
    fun setTag(filePath: String, tag: String): String
    fun getTag(filePath: String): String
    fun setDebug(enableDebug: Boolean)
}