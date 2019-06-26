package com.explori.spoiwo.natures.pptx

import java.io.{FileOutputStream, OutputStream}

import com.explori.spoiwo.model._

import org.apache.poi.xslf.usermodel._
//import org.apache.poi.xddf.usermodel.chart._

object Model2PptxConversions {
  
  //private[pptx] def convertChart(c: Chart) = new XDDFChart()
  
  //private[pptx] def convertPresentation(s: SlideShow) = new XDDFChart()
  
  /*
  private[pptx] def writeToExistingSlideShow(ss: SlideShow, slideShow: XMLSlideShow): XMLSlideShow = {
    ???
  }
  * 
  */
  
  implicit class XmlSlideShow(slideShow: SlideShow) {
    
    import Model2PptxConversions._
    
    //def writeToExisting(existingSlideShow: XMLSlideShow): Unit = writeToExistingSlideShow(slideShow, existingSlideShow)
    
    def convertToPptx(): XMLSlideShow = {
      val xmlSs = new XMLSlideShow()
      val defaultMaster = xmlSs.getSlideMasters.get(0)
      println(defaultMaster)
      slideShow.slides.foreach { slide => 
          val xslfSlide = slide.layout match {
            case Some(layout) =>
              val layoutConverted = Model2PptxEnumConversions.convertSlideLayout(layout.typ)
              val xslfLayout = defaultMaster.getLayout(layoutConverted)
              xmlSs.createSlide(xslfLayout)
            case None =>
              xmlSs.createSlide()
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
      
  }
  
  implicit class XslfSlideLayout(layout: com.explori.spoiwo.model.SlideLayout) {
    
    /*
    def convertToPptx(): XSLFSlideLayout = {
      new XSLFSlideLayout()
    }
    * 
    */
  }
  
  
  /*
  implicit class XslfChart(c: Chart) {
    def convertAsPptx(): XSLFChart = convertChart(c)
  }
  * 
  */

  
}