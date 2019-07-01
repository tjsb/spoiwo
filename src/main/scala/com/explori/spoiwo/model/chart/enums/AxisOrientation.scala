package com.explori.spoiwo.model.chart.enums

object AxisOrientation {
  
  lazy val MaxMin = AxisOrientation("MaxMin")
  lazy val MinMax = AxisOrientation("MinMax")
  
}

case class AxisOrientation private (value: String) {
  override def toString: String = value
}