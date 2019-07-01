package com.explori.spoiwo.model.chart.enums

object BarGrouping {
  
  lazy val Clustered = BarGrouping("Clustered")
  lazy val PercentStacked = BarGrouping("PercentStacked")
  lazy val Stacked = BarGrouping("Stacked")
  lazy val Standard = BarGrouping("Standard")
  
}

case class BarGrouping private (value: String) {
  override def toString: String = value
}