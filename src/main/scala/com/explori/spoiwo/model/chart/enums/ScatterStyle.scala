package com.explori.spoiwo.model.chart.enums

object ScatterStyle {
  
  lazy val Line = ScatterStyle("Line")
  lazy val LineMarker = ScatterStyle("LineMarker")
  lazy val Marker = ScatterStyle("Marker")
  lazy val None = ScatterStyle("None")
  lazy val Smooth = ScatterStyle("Smooth")
  lazy val SmoothMarker = ScatterStyle("SmoothMarker")
  
}

case class ScatterStyle private (value: String) {
  override def toString: String = value
}