package com.explori.spoiwo.model.chart

object BarChartData {
  
}

case class BarChartData(
    categoryAxis: CategoryAxis,
    valueAxes: Seq[ValueAxis],
    varyColors: Boolean = true,
    barDirection: enums.BarDirection = enums.BarDirection.Bar,
    barGrouping: enums.BarGrouping = enums.BarGrouping.Standard
) extends ChartData {
  
}