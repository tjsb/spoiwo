package com.explori.spoiwo.model.chart.enums

object AxisCrosses {
  
  lazy val AutoZero = AxisCrosses("AutoZero")
  lazy val Max = AxisCrosses("Max")
  lazy val Min = AxisCrosses("Min")
  
}

case class AxisCrosses private (value: String) {
  override def toString: String = value
}