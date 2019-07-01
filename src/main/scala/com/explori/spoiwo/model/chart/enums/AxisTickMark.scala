package com.explori.spoiwo.model.chart.enums

object AxisTickMark {
  
  lazy val Cross = AxisTickMark("Cross")
  lazy val In = AxisTickMark("In")
  lazy val Out = AxisTickMark("Out")
  lazy val None = AxisTickMark("None")
  
}

case class AxisTickMark private (value: String) {
  override def toString: String = value
}