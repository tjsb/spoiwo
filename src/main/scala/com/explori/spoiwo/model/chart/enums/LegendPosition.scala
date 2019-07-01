package com.explori.spoiwo.model.chart.enums

object LegendPosition {
  
  lazy val Bottom = LegendPosition("Bottom")
  lazy val Left = LegendPosition("Left")
  lazy val Right = LegendPosition("Right")
  lazy val Top = LegendPosition("Top")
  lazy val TopRight = LegendPosition("TopRight")
  
}

case class LegendPosition private (value: String) {
  override def toString: String = value
}