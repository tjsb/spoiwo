package com.explori.spoiwo.model.chart

object BarChartData {
  
}

case class BarChartData(
    categoryAxis: CategoryAxis,
    yAxis: ValueAxis,
    zAxis: Option[ValueAxis] = None,
    barDirection: enums.BarDirection = enums.BarDirection.Bar,
    barGrouping: enums.BarGrouping = enums.BarGrouping.Standard
) extends ChartData {
  
}