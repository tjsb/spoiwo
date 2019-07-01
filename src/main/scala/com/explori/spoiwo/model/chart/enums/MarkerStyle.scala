package com.explori.spoiwo.model.chart.enums

object MarkerStyle {
  
  lazy val Circle = MarkerStyle("Circle")
  lazy val Dash = MarkerStyle("Dash")
  lazy val Diamond = MarkerStyle("Diamond")
  lazy val Dot = MarkerStyle("Dot")
  lazy val None = MarkerStyle("None")
  lazy val Picture = MarkerStyle("Picture")
  lazy val Plus = MarkerStyle("Plus")
  lazy val Square = MarkerStyle("Square")
  lazy val Star = MarkerStyle("Star")
  lazy val Triangle = MarkerStyle("Triangle")
  lazy val X = MarkerStyle("X")
  
}

case class MarkerStyle private (value: String) {
  override def toString: String = value
}