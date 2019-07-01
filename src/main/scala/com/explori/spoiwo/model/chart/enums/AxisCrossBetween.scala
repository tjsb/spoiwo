package com.explori.spoiwo.model.chart.enums

object AxisCrossBetween {
  
  lazy val Between = AxisCrossBetween("Between")
  lazy val MidpointCategory = AxisCrossBetween("MidpointCategory")
  
}

case class AxisCrossBetween private (value: String) {
  override def toString: String = value
}