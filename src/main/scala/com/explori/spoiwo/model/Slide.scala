package com.explori.spoiwo.model

object Slide {
  
  val Blank = Slide()
  
  def apply(title: String = null, 
            layout: SlideLayout = null): Slide = 
              Slide(title = Option(title), 
                    layout = Option(layout),
                    charts = Nil
                    )
  
  def apply(layout: SlideLayout): Slide = Slide(layout)
  
}

case class Slide private (title: Option[String], layout: Option[SlideLayout], charts: Iterable[Chart]) {
  
  def withSlideLayout(layout: SlideLayout): Slide =
    copy(layout = Option(layout))
    
  def withSlideTitle(title: String): Slide =
    copy(title = Option(title))
}