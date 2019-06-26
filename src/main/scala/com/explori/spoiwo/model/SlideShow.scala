package com.explori.spoiwo.model

object SlideShow {
  
  val Empty: SlideShow = apply()
  
  def apply(slides: Iterable[Slide] = Nil,
            master: SlideMaster = null,
            layout: SlideLayout = null
      ): SlideShow = 
            SlideShow(slides = slides,
                      master = Option(master),
                      layout = Option(layout)
                      )
                      
   def apply(slides: Slide*): SlideShow =
    SlideShow(slides = slides)
  
}

case class SlideShow private (slides: Iterable[Slide],
                              master: Option[SlideMaster],
                              layout: Option[SlideLayout]
) {
  
  def withSlides(slides: Iterable[Slide]): SlideShow =
    copy(slides = slides)
  
}