package com.ctg.filetrace.util

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.FileWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/2/28 16:41
 */
class XmlProcessor(private val file: File) {

    private val doc: Document by lazy {
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(file)
            .also {
                it.documentElement.normalize()
            }
    }

    fun getDocInstance(): Document = doc

    fun getElementUnderTagByAttr(
        attrName: String,
        attrValue: String,
        targetTagName: String,
        parentTagName: String
    ): Element? {
        doc.getElementsByTagName(targetTagName).forEach { node ->
            if (node.nodeType == Node.ELEMENT_NODE &&
                (node as? Element)?.getAttribute(attrName) == attrValue &&
                (node.parentNode as? Element)?.tagName == parentTagName
            ) {
                return node
            }
        }
        return null
    }

    fun addOrReplaceElementUnder(
        element: Element,
        attrName: String,
        attrValue: String,
        parentTagName: String
    ): Boolean {
        val parentElement = doc.getElementsByTagName(parentTagName)?.takeIf {
            it.length > 0
        }?.let { it.item(0) as? Element } ?: return false
        parentElement.getElementsByTagName(element.tagName).forEach {
            if ((it as? Element)?.getAttribute(attrName) == attrValue) {
                parentElement.removeChild(it)
            }
        }
        parentElement.appendChild(element)
        return true
    }

    fun save(): Boolean {
        // write DOM back to the file
        val transformerFactory = TransformerFactory.newInstance()

        val xtransform = transformerFactory.newTransformer()
        FileWriter(file, false).use {
            xtransform.transform(DOMSource(doc), StreamResult(it))
        }
        return true
    }
}