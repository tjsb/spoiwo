package com.explori.spoiwo.model

object SlideLayout {
  
  def apply(typ: enums.SlideLayout = enums.SlideLayout.Blank,
            background: Background = null,
            master: SlideMaster = null
      ): SlideLayout = 
            SlideLayout(typ = typ,
                      background = Option(background),
                      master = Option(master)
                      )
  
}

case class SlideLayout private (typ: enums.SlideLayout,
                                background: Option[Background],
                                master: Option[SlideMaster]
                                ) {
  
}