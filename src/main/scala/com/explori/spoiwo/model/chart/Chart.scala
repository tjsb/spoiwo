package com.explori.spoiwo.model.chart

import com.explori.spoiwo.model.SlideContent
import org.apache.poi.xddf.usermodel.chart.XDDFChart

object Chart {
  
}

case class Chart private (
    title: String, 
    data: Seq[ChartData],
    xPos: Int = XDDFChart.DEFAULT_X, 
    yPos: Int = XDDFChart.DEFAULT_Y, 
    height: Int = XDDFChart.DEFAULT_HEIGHT, 
    width: Int = XDDFChart.DEFAULT_WIDTH,
    plotOnlyVisibleCells: Boolean = true,
    floor: Int = 1,
    backWall: Int = 1,
    sideWall: Int = 1,
    titleOverlay: Boolean = false,
    displayBlanksAs: enums.DisplayBlanks = enums.DisplayBlanks.Zero
    ) extends SlideContent {
  
}