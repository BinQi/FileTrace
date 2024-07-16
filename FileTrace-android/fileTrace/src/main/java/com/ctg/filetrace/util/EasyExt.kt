package com.ctg.filetrace.util

import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description 便捷扩展
 * @author jerryqwu
 * @date 2023/3/15 09:59
 */
inline fun NodeList?.forEach(action: (Node) -> Unit): Unit {
    if (null == this) return
    for (i in 0 until length) action(item(i))
}

inline fun NodeList?.firstOrNull(): Node? {
    return this?.takeIf {
        it.length > 0
    }?.item(0)
}