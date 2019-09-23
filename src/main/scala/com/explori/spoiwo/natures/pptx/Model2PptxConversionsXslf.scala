package com.explori.spoiwo.natures.pptx

import java.io.{FileOutputStream, OutputStream}
import java.util.regex.Pattern;

import javax.xml.namespace.QName

import java.awt.geom.Rectangle2D
import java.awt.Rectangle

import org.apache.poi.xslf.usermodel._
import org.apache.poi.xssf.usermodel._
import org.apache.poi.openxml4j.opc._
import org.apache.poi.ooxml.POIXMLDocumentPart
import org.apache.poi.ooxml.POIXMLRelation
import org.openxmlformats.schemas.drawingml.x2006.main._
import org.openxmlformats.schemas.drawingml.x2006.chart._
import org.apache.poi.util._
import org.apache.xmlbeans.XmlOptions
import org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS

import com.explori.spoiwo.model._
import com.explori.spoiwo.model.chart._

object Model2PptxConversionsXslf {
  
  implicit class XmlSlideShow(slideShow: SlideShow) {
    
    //def writeToExisting(existingSlideShow: XMLSlideShow): Unit = writeToExistingSlideShow(slideShow, existingSlideShow)
    
    def convertToPptx(): XMLSlideShow = {
      val xmlSs = new XMLSlideShow()
      val defaultMaster = xmlSs.getSlideMasters.get(0)
      //println(defaultMaster)
      val xslfSlideTuples = slideShow.slides.map { slide => 
          val xslfSlide = slide.layout match {
            case Some(layout) =>
              val layoutConverted = Model2PptxEnumConversions.convertSlideLayout(layout.typ)
              val xslfLayout = defaultMaster.getLayout(layoutConverted)
              xmlSs.createSlide(xslfLayout)
            case None =>
              xmlSs.createSlide()
          }
          (xslfSlide, slide)
        }
      xslfSlideTuples.foreach { t =>
        val (xslfSlide, slide) = t
        slide.content.foreach { content =>
          content match {
            case c: Chart =>
              val xslfChart = xmlSs.createChart()
              val rect2D = new java.awt.Rectangle(c.xPos, c.yPos, c.width, c.height)
              xslfSlide.addChart(xslfChart, rect2D);
              c match {
                case b: BarChart =>
                  drawBarChart(xslfChart, b)
                case p: PieChart =>
                  ???
              }

              
          }
        }
      }
      xmlSs
    }
    
    def writeToOutputStream[T <: OutputStream](stream: T): T =
      try {
        convertToPptx().write(stream)
        stream
      } finally {
        stream.flush()
        stream.close()
      }
      
    def saveAsPptx(fileName: String): Unit = writeToOutputStream(new FileOutputStream(fileName))
    
    def drawBarChart(xslfChart: XSLFChart, chart: BarChart) = {
      val yAxis = chart.data.yAxis
      val categoryValues = chart.data.categoryAxis.values
      val workbook = xslfChart.getWorkbook
      val sheet = workbook.getSheetAt(0);
      sheet.createRow(0);
      
      categoryValues.zipWithIndex.foreach { t =>
        val (cat, idx) = t
        sheet.getRow(0).createCell(idx+1).setCellValue(cat);
      }
      
      yAxis.data.zipWithIndex.foreach { t =>
        val (seriesData, r) = t
        sheet.createRow(r+1).createCell(0).setCellValue(seriesData.category);
        seriesData.values.zipWithIndex.foreach { t2 =>
          val (value, c) = t2
          sheet.getRow(r+1).createCell(c+1).setCellValue(value);
        }
      }
    }
    
  }
  
}