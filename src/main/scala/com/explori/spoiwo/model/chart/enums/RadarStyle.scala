package com.explori.spoiwo.model.chart.enums

object RadarStyle {
  
  lazy val Filled = RadarStyle("Filled")
  lazy val Marker = RadarStyle("Marker")
  lazy val Standard = RadarStyle("Standard")
  
}

case class RadarStyle private (value: String) {
  override def toString: String = value
}