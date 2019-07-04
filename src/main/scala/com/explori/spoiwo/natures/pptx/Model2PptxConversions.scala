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
              val myXSLFChartShape = createXSLFChart(xslfSlide)
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
    
    def createXSLFChart(slide: XSLFSlide): MyXslfChartShape = {
      val opcPackage = slide.getSlideShow().getPackage()
      val chartCount = opcPackage.getPartsByName(Pattern.compile("/ppt/charts/chart.*")).size() + 1
      val partName = PackagingURIHelper.createPartName("/ppt/charts/chart" + chartCount + ".xml")
      val part = opcPackage.createPart(partName, "application/vnd.openxmlformats-officedocument.drawingml.chart+xml")
      val myXSLFChart = new MyXslfChart(part)
      new MyXslfChartShape(slide, myXSLFChart);
    }
    
    def createSheet(sheet: XSSFSheet, data: ChartData) = {
      val cats = data.categoryAxis.values
      
    }
    
    def drawBarChart(myXSLFChartShape: MyXslfChartShape, chart: BarChart) {
      val workbook = myXSLFChartShape.getMyXSLFChart().getXSLFXSSFWorkbook().getXSSFWorkbook
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

      val chartSpace = myXSLFChartShape.getMyXSLFChart().getChartSpace();
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
    
    def drawPieChart(myXSLFChartShape: MyXslfChartShape, chart: PieChart) = {
      val workbook = myXSLFChartShape.getMyXSLFChart().getXSLFXSSFWorkbook().getXSSFWorkbook
      val sheet = workbook.getSheetAt(0);
      sheet.createRow(0).createCell(0).setCellValue("Cat");
      sheet.getRow(0).createCell(1).setCellValue("Val");
      for (r <- 1 to 3) {
       sheet.createRow(r).createCell(0).setCellValue("Cat" + r);
       sheet.getRow(r).createCell(1).setCellValue(10*r);
      }
    
      val chartSpace = myXSLFChartShape.getMyXSLFChart().getChartSpace();
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
    
    class MyXslfChart(part: PackagePart) extends POIXMLDocumentPart (part) {

      val oPCPackage = part.getPackage();
      val chartCount = oPCPackage.getPartsByName(Pattern.compile("/ppt/embeddings/.*.xlsx")).size() + 1;
      val partName = PackagingURIHelper.createPartName("/ppt/embeddings/Microsoft_Excel_Worksheet" + chartCount + ".xlsx");
      val xlsxpart = oPCPackage.createPart(partName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

      val myXSLFXSSFWorkbook = new MyXSLFXSSFWorkbook(xlsxpart);

      val rId = "rId" + this.getRelationParts().size() + 1
      val xSLFXSSFRelationPACKAGE = new XSLFXSSFRelation("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package");

      this.addRelation(rId, xSLFXSSFRelationPACKAGE, myXSLFXSSFWorkbook);

      val chartSpace = ChartSpaceDocument.Factory.newInstance().addNewChartSpace();
      val cTExternalData = chartSpace.addNewExternalData();
      cTExternalData.setId(rId);
      
      def getChartSpace(): CTChartSpace  = chartSpace

      def getXSLFXSSFWorkbook(): MyXSLFXSSFWorkbook  = myXSLFXSSFWorkbook
      
      override def commit = {
        val xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.`type`.getName().getNamespaceURI(), "chartSpace", "c"));
        val part = getPackagePart();
        val out = part.getOutputStream();
        chartSpace.save(out, xmlOptions);
        out.close();
      }
    }
    
    class MyXslfChartShape(slide: XSLFSlide, myXSLFChart: MyXslfChart) {
      
      import scala.collection.JavaConverters._
      
      val cNvPrName = "MyChart"
      val rId = "rId" + slide.getRelationParts().size() + 1
      slide.addRelation(rId, XSLFRelation.CHART, myXSLFChart)
      var cNvPrId = 1L
      var cNvPrNameCount = 1
      
      for (currGraphicalObjectFrame <- slide.getXmlObject().getCSld().getSpTree().getGraphicFrameList().asScala) {
        if (currGraphicalObjectFrame.getNvGraphicFramePr() != null) {
          if (currGraphicalObjectFrame.getNvGraphicFramePr().getCNvPr() != null) {
            cNvPrId += 1
            if (currGraphicalObjectFrame.getNvGraphicFramePr().getCNvPr().getName().startsWith(cNvPrName)) {
             cNvPrNameCount += 1
            }
          }
        }
      }
      
      /*
      val gofList = slide.getXmlObject().getCSld().getSpTree().getGraphicFrameList()
      gofList.asScala.foreach { currGraphicalObjectFrame =>
        Option(currGraphicalObjectFrame.getNvGraphicFramePr()) match {
          case Some(gf) =>
            println("graphicalobjectframe")
            cNvPrId += 1
            if (currGraphicalObjectFrame.getNvGraphicFramePr().getCNvPr().getName().startsWith(cNvPrName)) {
              cNvPrNameCount += 1
            }
          case None =>
        }
      }
      * 
      */
      private val graphicalObjectFrame = slide.getXmlObject().getCSld().getSpTree().addNewGraphicFrame();
      val cTGraphicalObjectFrameNonVisual = graphicalObjectFrame.addNewNvGraphicFramePr();
      cTGraphicalObjectFrameNonVisual.addNewCNvGraphicFramePr();
      cTGraphicalObjectFrameNonVisual.addNewNvPr();
      val cTNonVisualDrawingProps = cTGraphicalObjectFrameNonVisual.addNewCNvPr();
      cTNonVisualDrawingProps.setId(cNvPrId);
      cTNonVisualDrawingProps.setName(cNvPrName + cNvPrNameCount);
      val graphicalObject = graphicalObjectFrame.addNewGraphic();
      val graphicalObjectData = CTGraphicalObjectData.Factory.parse(
        "<c:chart xmlns:c=\"http://schemas.openxmlformats.org/drawingml/2006/chart\" "
        +"xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" "
        +"r:id=\"" + rId + "\"/>"
      );
      graphicalObjectData.setUri("http://schemas.openxmlformats.org/drawingml/2006/chart");
      graphicalObject.setGraphicData(graphicalObjectData);
      this.setAnchor(new Rectangle())
      
      def setAnchor(anchor: Rectangle2D) = {
        val xfrm = if (graphicalObjectFrame.getXfrm() != null) graphicalObjectFrame.getXfrm() else graphicalObjectFrame.addNewXfrm()
        val off = if (xfrm.isSetOff()) xfrm.getOff() else xfrm.addNewOff()
        val x = Units.toEMU(anchor.getX());
        val y = Units.toEMU(anchor.getY());
        off.setX(x);
        off.setY(y);
        val ext = if (xfrm.isSetExt()) xfrm.getExt() else xfrm.addNewExt()
        val cx = Units.toEMU(anchor.getWidth());
        val cy = Units.toEMU(anchor.getHeight());
        ext.setCx(cx);
        ext.setCy(cy);
      }
      
      def getMyXSLFChart() = this.myXSLFChart
    }
      
  }
  
  class MyXSLFXSSFWorkbook(part: PackagePart) extends POIXMLDocumentPart (part) {
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
  
  class XSLFXSSFRelation(rel: String) extends POIXMLRelation (null, rel, null)
  
  
  
}