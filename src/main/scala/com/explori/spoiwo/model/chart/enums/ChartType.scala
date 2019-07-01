package com.explori.spoiwo.model.chart.enums

object ChartType {
  
  lazy val Bar = ChartType("Bar")
  lazy val Line = ChartType("Line")
  lazy val Pie = ChartType("Pie")
  lazy val Radar = ChartType("Radar")
  lazy val Scatter = ChartType("Scatter")
  
}

case class ChartType private (value: String) {
  override def toString: String = value
}