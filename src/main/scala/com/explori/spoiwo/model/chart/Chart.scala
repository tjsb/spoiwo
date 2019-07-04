package com.explori.spoiwo.model.chart

import com.explori.spoiwo.model.SlideContent
import org.apache.poi.xddf.usermodel.chart.XDDFChart

trait Chart extends SlideContent {
  
  val title: String
  val data: ChartData
  val xPos: Int
  val yPos: Int
  val height: Int
  val width: Int
  val plotOnlyVisibleCells: Boolean
  val floor: Int
  val backWall: Int
  val sideWall: Int
  val titleOverlay: Boolean
  val displayBlanksAs: enums.DisplayBlanks
  
}