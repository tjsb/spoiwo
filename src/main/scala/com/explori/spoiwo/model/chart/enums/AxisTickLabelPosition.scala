package com.explori.spoiwo.model.chart.enums

object AxisTickLabelPosition {
  
  lazy val High = AxisTickLabelPosition("High")
  lazy val Low = AxisTickLabelPosition("Low")
  lazy val NextTo = AxisTickLabelPosition("NextTo")
  lazy val None = AxisTickLabelPosition("None")
  
}

case class AxisTickLabelPosition private (value: String) {
  override def toString: String = value
}