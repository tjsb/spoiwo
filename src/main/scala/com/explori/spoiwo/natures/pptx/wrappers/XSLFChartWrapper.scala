package com.explori.spoiwo.natures.pptx.wrappers

import java.util.regex.Pattern

import org.apache.poi.ooxml.POIXMLDocumentPart
import org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS
import org.apache.poi.openxml4j.opc.PackagePart
import org.apache.poi.openxml4j.opc.PackagingURIHelper
import org.apache.xmlbeans.XmlOptions
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace
import org.openxmlformats.schemas.drawingml.x2006.chart.ChartSpaceDocument

import javax.xml.namespace.QName

class XSLFChartWrapper(part: PackagePart) extends POIXMLDocumentPart (part) {

      val oPCPackage = part.getPackage();
      val chartCount = oPCPackage.getPartsByName(Pattern.compile("/ppt/embeddings/.*.xlsx")).size() + 1;
      val partName = PackagingURIHelper.createPartName("/ppt/embeddings/Microsoft_Excel_Worksheet" + chartCount + ".xlsx");
      val xlsxpart = oPCPackage.createPart(partName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

      val myXSLFXSSFWorkbook = new XSLFXSSFWorkbookWrapper(xlsxpart);

      val rId = "rId" + this.getRelationParts().size() + 1
      val xSLFXSSFRelationPackage = new XSLFXSSFRelationWrapper("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package");

      this.addRelation(rId, xSLFXSSFRelationPackage, myXSLFXSSFWorkbook);

      val chartSpace = ChartSpaceDocument.Factory.newInstance().addNewChartSpace();
      val cTExternalData = chartSpace.addNewExternalData();
      cTExternalData.setId(rId);
      
      def getCTChartSpace(): CTChartSpace  = chartSpace

      def getXSLFXSSFWorkbookWrapper(): XSLFXSSFWorkbookWrapper  = myXSLFXSSFWorkbook
      
      override def commit = {
        val xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.`type`.getName().getNamespaceURI(), "chartSpace", "c"));
        val part = getPackagePart();
        val out = part.getOutputStream();
        chartSpace.save(out, xmlOptions);
        out.close();
      }
    }