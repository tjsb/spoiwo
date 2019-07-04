package com.explori.spoiwo.model.chart

object BarChart {
  
}

case class BarChart(
    title: String, 
    data: BarChartData,
    xPos: Int = 50,
    yPos: Int = 50, 
    height: Int = 450, 
    width: Int = 600,
    varyColors: Boolean = true,
    plotOnlyVisibleCells: Boolean = true,
    floor: Int = 1,
    backWall: Int = 1,
    sideWall: Int = 1,
    titleOverlay: Boolean = false,
    displayBlanksAs: enums.DisplayBlanks = enums.DisplayBlanks.Zero
    ) extends Chart {
  
}