package com.explori.spoiwo.model.chart.enums

object BarDirection {
  
  lazy val Bar = BarDirection("Bar")
  lazy val Col = BarDirection("Col")
  
}

case class BarDirection private (value: String) {
  override def toString: String = value
}