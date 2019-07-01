package com.explori.spoiwo.model.chart.enums

object LayoutTarget {
  
  lazy val Inner = LayoutTarget("Inner")
  lazy val Outer = LayoutTarget("Outer")
  
}

case class LayoutTarget private (value: String) {
  override def toString: String = value
}