package com.explori.spoiwo.natures.pptx.wrappers

import java.awt.Rectangle
import java.awt.geom.Rectangle2D
import java.util.regex.Pattern

import org.apache.poi.openxml4j.opc.PackagingURIHelper
import org.apache.poi.util.Units
import org.apache.poi.xslf.usermodel.XSLFRelation
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData

object XSLFChartShapeWrapper {
  
  def apply(slide: XSLFSlide): XSLFChartShapeWrapper = {
    val opcPackage = slide.getSlideShow().getPackage()
    val chartCount = opcPackage.getPartsByName(Pattern.compile("/ppt/charts/chart.*")).size() + 1
    val partName = PackagingURIHelper.createPartName("/ppt/charts/chart" + chartCount + ".xml")
    val part = opcPackage.createPart(partName, "application/vnd.openxmlformats-officedocument.drawingml.chart+xml")
    val myXSLFChart = new XSLFChartWrapper(part)
    new XSLFChartShapeWrapper(slide, myXSLFChart)
  }
  
}

class XSLFChartShapeWrapper(slide: XSLFSlide, myXSLFChart: XSLFChartWrapper) {
      
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
  
  def getXSLFChartWrapper() = this.myXSLFChart
}