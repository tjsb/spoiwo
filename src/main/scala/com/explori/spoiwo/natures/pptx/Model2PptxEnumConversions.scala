package com.explori.spoiwo.natures.pptx

import com.explori.spoiwo.model.enums._
import org.apache.poi.xslf.usermodel.SlideLayout._

object Model2PptxEnumConversions {
  
  def convertSlideLayout(sl: SlideLayout): org.apache.poi.xslf.usermodel.SlideLayout = {
    import SlideLayout._

    sl match {
      case Blank              => BLANK
      case TitleAndContent    => TITLE_AND_CONTENT
      case SlideLayout(value) =>
        throw new IllegalArgumentException(s"Unable to convert SlideLayout=$value to PPTX - unsupported enum!")
    }
  }
  
}