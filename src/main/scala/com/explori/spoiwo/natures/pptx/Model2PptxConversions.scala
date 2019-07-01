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
              val myXSLFChartShape = createXSLFChart(xslfSlide);
              myXSLFChartShape.setAnchor(new Rectangle(c.xPos, c.yPos, c.height, c.width));
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
    
    def drawPieChart(myXSLFChartShape: MyXslfChartShape, slide: Slide) = {
      val workbook = myXSLFChartShape.getMyXSLFChart().getXSLFXSSFWorkbook().getXSSFWorkbook
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

      val rId = "rId" + (this.getRelationParts().size()+1);
      val xSLFXSSFRelationPACKAGE = new XSLFXSSFRelation("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package");

      this.addRelation(rId, xSLFXSSFRelationPACKAGE, myXSLFXSSFWorkbook);

      val chartSpace = ChartSpaceDocument.Factory.newInstance().addNewChartSpace();
      val cTExternalData = chartSpace.addNewExternalData();
      cTExternalData.setId(rId);
      
      def getChartSpace(): CTChartSpace  = chartSpace

      def getXSLFXSSFWorkbook(): MyXSLFXSSFWorkbook  = myXSLFXSSFWorkbook

      override def commit() = {
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
      val gofList = slide.getXmlObject().getCSld().getSpTree().getGraphicFrameList()
      gofList.asScala.foreach { currGraphicalObjectFrame =>
        Option(currGraphicalObjectFrame.getNvGraphicFramePr()) match {
          case Some(gf) =>
            cNvPrId += 1
            if (currGraphicalObjectFrame.getNvGraphicFramePr().getCNvPr().getName().startsWith(cNvPrName)) {
              cNvPrNameCount += 1
            }
          case None =>
        }
      }
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