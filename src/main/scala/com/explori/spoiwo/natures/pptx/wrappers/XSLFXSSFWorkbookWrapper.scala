package com.explori.spoiwo.natures.pptx.wrappers

import org.apache.poi.ooxml.POIXMLDocumentPart
import org.apache.poi.openxml4j.opc.PackagePart
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class XSLFXSSFWorkbookWrapper(part: PackagePart) extends POIXMLDocumentPart (part) {
    val workbook = new XSSFWorkbook();
    val sheet = workbook.createSheet()
    
    def getXSSFWorkbook = workbook
    
    override def commit() = {
      val part = getPackagePart()
      val out = part.getOutputStream()
      workbook.write(out);
      workbook.close();
      out.close();
    }
  }