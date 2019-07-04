package com.explori.spoiwo.model.chart

object PieChart {
  
}

case class PieChart(
    title: String, 
    data: PieChartData,
    xPos: Int = 50,
    yPos: Int = 50, 
    height: Int = 500, 
    width: Int = 500,
    plotOnlyVisibleCells: Boolean = true,
    floor: Int = 1,
    backWall: Int = 1,
    sideWall: Int = 1,
    titleOverlay: Boolean = false,
    displayBlanksAs: enums.DisplayBlanks = enums.DisplayBlanks.Zero
    ) extends Chart {
  
}