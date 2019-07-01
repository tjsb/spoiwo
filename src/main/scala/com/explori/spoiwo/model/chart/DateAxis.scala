package com.explori.spoiwo.model.chart

object DateAxis {
  
}

case class DateAxis(
    title: String,
    position: enums.AxisPosition,
    orientation: enums.AxisOrientation,
    crosses: enums.AxisCrosses = enums.AxisCrosses.AutoZero,
    crossAxis: Option[ChartAxis] = None,
    majorUnit: Option[Double] = None,
    minorUnit: Option[Double] = None,
    maximum: Option[Double] = None,
    minimum: Option[Double] = None,
    numberFormat: Option[String] = None,
    visible: Boolean = true  
  ) extends ChartAxis {
  
}