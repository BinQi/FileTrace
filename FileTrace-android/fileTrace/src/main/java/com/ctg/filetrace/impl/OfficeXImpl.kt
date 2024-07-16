package com.ctg.filetrace.impl

import com.ctg.filetrace.util.FileUtils
import com.ctg.filetrace.util.Logger
import com.ctg.filetrace.util.XmlProcessor
import com.ctg.filetrace.util.firstOrNull
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File

/**
 * Copyright (C) @2021 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * @description
 * @author jerryqwu
 * @date 2023/3/15 10:03
 */
class OfficeXImpl : ZipImpl() {

    override fun setToXml(unzipDir: File, tag: String): Boolean {
        val relsXml = File(unzipDir.absolutePath + File.separator + RELS_PATH)
        if (relsXml.exists().not() || relsXml.isDirectory) {
            FileUtils.delete(relsXml)
            FileUtils.writeString(relsXml, DEFAULT_RES, false)
            Logger.d("OfficeXImpl: Rels Xml not exist, write default content")
        }
        val relsResult = XmlProcessor(relsXml).takeIf {
            it.getElementUnderTagByAttr(
                ATTR_TARGET,
                DEFAULT_TARGET,
                TAG_RELATION,
                TAG_RELATIONS
            ) == null
        }?.run {
            addOrReplaceElementUnder(
                createRelsElement(getDocInstance()),
                ATTR_TARGET,
                DEFAULT_TARGET,
                TAG_RELATIONS
            ) && save()
        }
        when (relsResult) {
            true -> Logger.d("OfficeXImpl: suffessfully set To Rels Xml")
            null -> Logger.d("OfficeXImpl: No need to set Rels Xml")
            false -> {
                Logger.e("OfficeXImpl: set To Rels Xml fail")
                return false
            }
        }

        val customXml = File(unzipDir.absolutePath + File.separator + CUSTOM_XML_PATH)
        if (customXml.exists().not() || customXml.isDirectory) {
            FileUtils.delete(customXml)
            FileUtils.writeString(customXml, DEFAULT_CUSTOM_XML_CONTENT, false)
        }

        return XmlProcessor(customXml).run {
            addOrReplaceElementUnder(
                createCtgFTagElement(getDocInstance(), tag),
                ATTR_NAME,
                CTG_F_TAG,
                TAG_PROPERTIES
            ) && save()
        }
    }

    override fun readFromXml(unzipDir: File): String? {
        val customXml = File(unzipDir.absolutePath + File.separator + CUSTOM_XML_PATH)
        if (customXml.exists().not() || customXml.isDirectory) {
            FileUtils.writeString(customXml, DEFAULT_CUSTOM_XML_CONTENT, false)
        }
        return XmlProcessor(customXml).run {
            getElementUnderTagByAttr(ATTR_NAME, CTG_F_TAG, TAG_PROPERTY, TAG_PROPERTIES)
        }?.run {
            getElementsByTagName(TAG_STR).firstOrNull()?.textContent
        }
    }

    private fun createRelsElement(doc: Document): Element {
        return doc.createElement(TAG_RELATION).apply {
            setAttribute(ATTR_ID, DEFAULT_ID)
            setAttribute(ATTR_TYPE, DEFAULT_TYPE)
            setAttribute(ATTR_TARGET, DEFAULT_TARGET)
        }
    }

    private fun createCtgFTagElement(doc: Document, tag: String): Element {
        return doc.createElement(TAG_PROPERTY).apply {
            setAttribute(ATTR_FMTID, DEFAULT_FMTID)
            setAttribute(ATTR_PID, DEFAULT_PID)
            setAttribute(ATTR_NAME, CTG_F_TAG)
            val e = doc.createElement(TAG_STR)
            e.appendChild(doc.createTextNode(tag))
            appendChild(e)
        }
    }

    companion object {
        private const val CTG_F_TAG = "ctgFTag"
        private const val CUSTOM_XML_PATH = "docProps/custom.xml"
        private const val RELS_PATH = "_rels/.rels"

        private const val DEFAULT_FMTID = "{D5CDD505-2E9C-101B-9397-08002B2CF9AE}"
        private const val DEFAULT_PID = "2"
        private const val DEFAULT_CUSTOM_XML_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Properties xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\"\n" +
                "        xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/custom-properties\">\n" +
                "    <property fmtid=\"${DEFAULT_FMTID}\" pid=\"${DEFAULT_PID}\" name=\"${CTG_F_TAG}\">\n" +
                "        <vt:lpwstr></vt:lpwstr>\n" +
                "    </property>\n" +
                "</Properties>"

        private const val DEFAULT_ID = "rId4"
        private const val DEFAULT_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/custom-properties"
        private const val DEFAULT_TARGET = "docProps/custom.xml"
        private const val DEFAULT_RES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "    <Relationship Id=\"rId1\"\n" +
                "            Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\"\n" +
                "            Target=\"docProps/app.xml\" />\n" +
                "    <Relationship Id=\"rId2\"\n" +
                "            Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\"\n" +
                "            Target=\"docProps/core.xml\" />\n" +
                "<Relationship Id=\"rId3\"\n" +
                "            Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\"\n" +
                "            Target=\"word/document.xml\" />" +
                "    <Relationship Id=\"${DEFAULT_ID}\"\n" +
                "            Type=\"${DEFAULT_TYPE}\"\n" +
                "            Target=\"${DEFAULT_TARGET}\" />\n" +
                "</Relationships>"

        private const val TAG_RELATIONS = "Relationships"
        private const val TAG_RELATION = "Relationship"
        private const val TAG_PROPERTIES = "Properties"
        private const val TAG_PROPERTY = "property"
        private const val TAG_STR = "vt:lpwstr"
        private const val ATTR_FMTID = "fmtid"
        private const val ATTR_PID = "pid"
        private const val ATTR_NAME = "name"
        private const val ATTR_ID = "Id"
        private const val ATTR_TYPE = "Type"
        private const val ATTR_TARGET = "Target"
    }
}