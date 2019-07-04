package com.explori.spoiwo.model.chart

object CategoryAxis {
  
}

case class CategoryAxis(
    legend: String,
    values: Seq[String],
    position: enums.AxisPosition = enums.AxisPosition.Bottom,
    orientation: enums.AxisOrientation = enums.AxisOrientation.MinMax,
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