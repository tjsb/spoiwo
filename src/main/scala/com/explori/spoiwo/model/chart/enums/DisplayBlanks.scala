package com.explori.spoiwo.model.chart.enums

object DisplayBlanks {
  
  lazy val Gap = DisplayBlanks("Gap")
  lazy val Span = DisplayBlanks("Span")
  lazy val Zero = DisplayBlanks("Zero")
  
}

case class DisplayBlanks private (value: String) {
  override def toString: String = value
}