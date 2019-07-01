package com.explori.spoiwo.model

object Slide {
  
  val Blank = Slide()
  
  def apply(content: Iterable[SlideContent] = Iterable.empty, 
            layout: SlideLayout = null): Slide = 
              Slide(content = content, 
                    layout = Option(layout)
                    )
  
  def apply(content: SlideContent*): Slide = Slide(content = content)
  
}

case class Slide private (content: Iterable[SlideContent], layout: Option[SlideLayout]) {
  
  def withSlideLayout(layout: SlideLayout): Slide =
    copy(layout = Option(layout))
    
  def withSlideContent(content: Iterable[SlideContent]): Slide =
    copy(content = content)
}