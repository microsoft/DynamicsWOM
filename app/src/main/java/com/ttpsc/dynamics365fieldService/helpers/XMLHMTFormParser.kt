package com.ttpsc.dynamics365fieldService.helpers

import com.ttpsc.dynamics365fieldService.dal.models.dynamics.customFields.DalCustomField
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XMLHMTFormParser {
    fun readXml(xmlString: String): Document {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        return dBuilder.parse(InputSource(StringReader(xmlString)))
    }

    fun getFieldNameAndDescriptionMape(doc: Document): List<DalCustomField> {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        val xpath = "/form/tabs/tab/columns/column/sections/section/rows/row/cell"

        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList

        val fieldNameDescription: MutableList<DalCustomField> = mutableListOf()
        for (i in 0..itemsTypeT1.length - 1) {
            var description = ""
            var fieldName = ""
            val item = itemsTypeT1.item(i) //search label and field name from this item
            //search for label
            for (cellChildIndex in 0..item.childNodes.length - 1) {
                val cellChild = item.childNodes.item(cellChildIndex)
                if (cellChild.nodeName == "labels") {
                    for (labelChildIndex in 0..cellChild.childNodes.length - 1) {
                        val labelsChild = cellChild.childNodes.item(labelChildIndex)
                        if (labelsChild.nodeName == "label") {
                            for (labelAttributeIndex in 0..labelsChild.attributes.length - 1) {
                                var labelAttribute =
                                    labelsChild.attributes.item(labelAttributeIndex)
                                if (labelAttribute.nodeName == "description") {
                                    description = labelAttribute.nodeValue
                                }
                            }
                        }
                    }
                }
            }
            //search for field name
            for (cellChildIndex in 0..item.childNodes.length - 1) {
                val cellChild = item.childNodes.item(cellChildIndex)
                if (cellChild.nodeName == "control") {
                    for (cellAttributeIndex in 0..cellChild.attributes.length - 1) {
                        var controlAttribute =
                            cellChild.attributes.item(cellAttributeIndex)
                        if (controlAttribute.nodeName == "datafieldname") {
                            fieldName = controlAttribute.nodeValue
                        }
                    }
                }
            }

            if (fieldName.isNullOrEmpty() == false && description.isNullOrEmpty() == false) {
                fieldNameDescription.add(DalCustomField(fieldName, description))
            }
        }

        return fieldNameDescription
    }
}