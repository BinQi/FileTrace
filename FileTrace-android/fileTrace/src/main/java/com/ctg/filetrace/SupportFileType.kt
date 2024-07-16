package com.ctg.filetrace

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/24 09:34
 */
enum class SupportFileType(val suffixList: List<String>) {
    PDF(listOf("pdf")),
    ZIP(listOf("zip")),
    OFFICE_X(listOf("docx", "pptx", "xlsx"));

    companion object {
        fun fromSuffix(suffix: String): SupportFileType? {
            return SupportFileType.values().firstOrNull {
                it.suffixList.contains(suffix)
            }
        }
    }
}