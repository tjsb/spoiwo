package com.explori.spoiwo.model.chart

trait ChartData {
  
  val categoryAxis: CategoryAxis
  val valueAxes: Seq[ValueAxis]
  val varyColors: Boolean
  
  
}