package com.explori.spoiwo.natures.pptx

import java.awt.Rectangle
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.regex.Pattern

import org.apache.poi.openxml4j.opc.PackagingURIHelper
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos

import com.explori.spoiwo.model.SlideShow
import com.explori.spoiwo.model.chart.BarChart
import com.explori.spoiwo.model.chart.Chart
import com.explori.spoiwo.model.chart.ChartData
import com.explori.spoiwo.model.chart.PieChart
import com.explori.spoiwo.natures.pptx.wrappers.XSLFChartShapeWrapper
import com.explori.spoiwo.natures.pptx.wrappers.XSLFChartWrapper

object Model2PptxConversions {
  
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
              val myXSLFChartShape = XSLFChartShapeWrapper(xslfSlide)
              myXSLFChartShape.setAnchor(new Rectangle(c.xPos, c.yPos, c.width, c.height))
              c match {
                case b: BarChart =>
                  drawBarChart(myXSLFChartShape, b)
                case p: PieChart =>
                  drawPieChart(myXSLFChartShape, p)
              }
              
          }
        }
      }
      xmlSs
    }
    
    def createSheet(sheet: XSSFSheet, data: ChartData) = {
      val cats = data.categoryAxis.values
      
    }
    
    def drawBarChart(myXSLFChartShape: XSLFChartShapeWrapper, chart: BarChart) {
      val workbook = myXSLFChartShape.getXSLFChartWrapper().getXSLFXSSFWorkbookWrapper().getXSSFWorkbook
      val yAxis = chart.data.yAxis
      val categoryValues = chart.data.categoryAxis.values
      
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

      val chartSpace = myXSLFChartShape.getXSLFChartWrapper().getCTChartSpace();
      val cTChart = chartSpace.addNewChart();
      val cTPlotArea = cTChart.addNewPlotArea();
      val cTBarChart = cTPlotArea.addNewBarChart();
      cTBarChart.addNewVaryColors().setVal(chart.varyColors);
      cTBarChart.addNewBarDir().setVal(STBarDir.COL);
      
      //add series - these are rows in the excel sheet
      for (r <- 1 to yAxis.data.length) {
        val seriesData = yAxis.data(r-1)
        val cTBarSer = cTBarChart.addNewSer();
        var cTStrRef = cTBarSer.addNewTx().addNewStrRef();
        cTStrRef.setF("Sheet0!$A$" + (r+1));
        cTStrRef.addNewStrCache().addNewPtCount().setVal(1);
        var cTStrVal = cTStrRef.getStrCache().addNewPt();
        cTStrVal.setIdx(0);
        cTStrVal.setV(seriesData.category);
        cTBarSer.addNewIdx().setVal(r-1);
        
        //categories
        val cttAxDataSource = cTBarSer.addNewCat();
        cTStrRef = cttAxDataSource.addNewStrRef();
        cTStrRef.setF("Sheet0!$B$1:$D$1"); //categories
        cTStrRef.addNewStrCache().addNewPtCount().setVal(3);
        for (c <- 1 to categoryValues.length) {
          cTStrVal = cTStrRef.getStrCache().addNewPt();
          cTStrVal.setIdx(c-1);
          cTStrVal.setV(categoryValues(c-1));
        }
        
        val ctNumDataSource = cTBarSer.addNewVal();
        var cTNumRef = ctNumDataSource.addNewNumRef();
        cTNumRef.setF("Sheet0!$B$" + (r+1) + ":$D$" + (r+1));
        cTNumRef.addNewNumCache().addNewPtCount().setVal(3);
        for (c <- 1 to categoryValues.length) {
          val cTNumVal = cTNumRef.getNumCache().addNewPt();
          cTNumVal.setIdx(c-1);
          val thisValue = seriesData.values(c-1)
          cTNumVal.setV("" + thisValue)
        }
      }
      
      //telling the BarChart that it has axes and giving them Ids
      cTBarChart.addNewAxId().setVal(123456);
      cTBarChart.addNewAxId().setVal(123457);
    
      //cat axis
      val cTCatAx = cTPlotArea.addNewCatAx(); 
      cTCatAx.addNewAxId().setVal(123456); //id of the cat axis
      var cTScaling = cTCatAx.addNewScaling();
      cTScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
      cTCatAx.addNewDelete().setVal(false);
      cTCatAx.addNewAxPos().setVal(STAxPos.B);
      cTCatAx.addNewCrossAx().setVal(123457); //id of the val axis
      cTCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
  
      //val axis
      val cTValAx = cTPlotArea.addNewValAx(); 
      cTValAx.addNewAxId().setVal(123457); //id of the val axis
      cTScaling = cTValAx.addNewScaling();
      cTScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
      cTValAx.addNewDelete().setVal(false);
      cTValAx.addNewAxPos().setVal(STAxPos.L);
      cTValAx.addNewCrossAx().setVal(123456); //id of the cat axis
      cTValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
  
      //legend
      val cTLegend = cTChart.addNewLegend();
      cTLegend.addNewLegendPos().setVal(STLegendPos.B);
      cTLegend.addNewOverlay().setVal(false);

    }
    
    def drawPieChart(myXSLFChartShape: XSLFChartShapeWrapper, chart: PieChart) = {
      val workbook = myXSLFChartShape.getXSLFChartWrapper().getXSLFXSSFWorkbookWrapper().getXSSFWorkbook
      val sheet = workbook.getSheetAt(0);
      sheet.createRow(0).createCell(0).setCellValue("Cat");
      sheet.getRow(0).createCell(1).setCellValue("Val");
      for (r <- 1 to 3) {
       sheet.createRow(r).createCell(0).setCellValue("Cat" + r);
       sheet.getRow(r).createCell(1).setCellValue(10*r);
      }
    
      val chartSpace = myXSLFChartShape.getXSLFChartWrapper().getCTChartSpace();
      val cTPieChart = chartSpace.addNewChart().addNewPlotArea().addNewPieChart();
      cTPieChart.addNewVaryColors().setVal(true);
      val cTPieSer = cTPieChart.addNewSer();
      cTPieSer.addNewIdx().setVal(0);
      var cTStrRef = cTPieSer.addNewTx().addNewStrRef();
      cTStrRef.setF("Sheet0!$B$1");
      cTStrRef.addNewStrCache().addNewPtCount().setVal(1);
      var cTStrVal = cTStrRef.getStrCache().addNewPt();
      cTStrVal.setIdx(0);
      cTStrVal.setV("Val");
    
      cTStrRef = cTPieSer.addNewCat().addNewStrRef();
      cTStrRef.setF("Sheet0!$A$2:$A$4");
    
      cTStrRef.addNewStrCache().addNewPtCount().setVal(3);
      for (r <- 1 to 3) { 
       cTStrVal = cTStrRef.getStrCache().addNewPt();
       cTStrVal.setIdx(r-1);
       cTStrVal.setV("Cat" + r);
      }
    
      val cTNumRef = cTPieSer.addNewVal().addNewNumRef();
      cTNumRef.setF("Sheet0!$B$2:$B$4");
    
      cTNumRef.addNewNumCache().addNewPtCount().setVal(3);
      for (r <- 1 to 3) { 
       val cTNumVal = cTNumRef.getNumCache().addNewPt();
       cTNumVal.setIdx(r-1);
       cTNumVal.setV("" + (10*r));
      }
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
  
}