package com.explori.spoiwo.model.chart.enums

object LayoutMode {
  
  lazy val Edge = LayoutMode("Edge")
  lazy val Factor = LayoutMode("Factor")
  
}

case class LayoutMode private (value: String) {
  override def toString: String = value
}