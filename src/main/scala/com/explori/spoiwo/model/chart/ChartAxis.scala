package com.explori.spoiwo.model.chart

trait ChartAxis {
  
  val position: enums.AxisPosition
  val legend: String
  val majorUnit: Option[Double]
  val minorUnit: Option[Double]
  val maximum: Option[Double]
  val minimum: Option[Double]
  val numberFormat: Option[String]
  val orientation: enums.AxisOrientation
  val crosses: enums.AxisCrosses
  val visible: Boolean
  
}