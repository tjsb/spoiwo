package com.explori.spoiwo.model.enums

/*
 * See https://poi.apache.org/apidocs/dev/org/apache/poi/xslf/usermodel/SlideLayout.html
 */

object SlideLayout {
  
  lazy val Blank = SlideLayout("Blank")
  lazy val TitleAndContent = SlideLayout("TitleAndContent")
  lazy val Chart = SlideLayout("Chart")
  lazy val ChartAndText = SlideLayout("ChartAndText")
  
}

case class SlideLayout private (value: String) {
  override def toString: String = value
}