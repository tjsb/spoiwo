package com.explori.spoiwo.model.chart

object PieChartData {
  
}

case class PieChartData(
    categoryAxis: CategoryAxis,
    valueAxes: Seq[ValueAxis],
    varyColors: Boolean = true
) extends ChartData {
  
}