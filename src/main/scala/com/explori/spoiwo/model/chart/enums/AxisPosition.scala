package com.explori.spoiwo.model.chart.enums

object AxisPosition {
  
  lazy val Bottom = AxisPosition("Bottom")
  lazy val Left = AxisPosition("Left")
  lazy val Right = AxisPosition("Right")
  lazy val Top = AxisPosition("Top")
  
}

case class AxisPosition private (value: String) {
  override def toString: String = value
}