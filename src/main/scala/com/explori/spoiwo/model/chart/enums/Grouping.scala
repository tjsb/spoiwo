package com.explori.spoiwo.model.chart.enums

object Grouping {
  
  lazy val PercentStacked = Grouping("PercentStacked")
  lazy val Stacked = Grouping("Stacked")
  lazy val Standard = Grouping("Standard")
  
}

case class Grouping private (value: String) {
  override def toString: String = value
}