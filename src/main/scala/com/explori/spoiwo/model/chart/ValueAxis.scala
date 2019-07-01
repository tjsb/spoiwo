package com.explori.spoiwo.model.chart

object ValueAxis {
  
}

case class ValueAxis(
    title: String,
    data: Seq[SeriesData],
    position: enums.AxisPosition = enums.AxisPosition.Left,
    orientation: enums.AxisOrientation = enums.AxisOrientation.MinMax,
    crosses: enums.AxisCrosses = enums.AxisCrosses.AutoZero,
    crossAxis: Option[ChartAxis] = None,
    crossBetween: enums.AxisCrossBetween = enums.AxisCrossBetween.Between,
    majorUnit: Option[Double] = None,
    minorUnit: Option[Double] = None,
    maximum: Option[Double] = None,
    minimum: Option[Double] = None,
    numberFormat: Option[String] = None,
    visible: Boolean = true
  ) extends ChartAxis {
  
}