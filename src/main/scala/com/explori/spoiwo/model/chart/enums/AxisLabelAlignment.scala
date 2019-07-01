package com.explori.spoiwo.model.chart.enums

object AxisLabelAlignment {
  
  lazy val Center = AxisLabelAlignment("Center")
  lazy val Left = AxisLabelAlignment("Left")
  lazy val Right = AxisLabelAlignment("Right")
  
}

case class AxisLabelAlignment private (value: String) {
  override def toString: String = value
}