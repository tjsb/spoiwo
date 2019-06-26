package com.explori.spoiwo.model

import org.scalatest.{FunSuite, Matchers}

import com.explori.spoiwo.natures.pptx.Model2PptxConversions._

class DevTests extends FunSuite with Matchers {
  
  /*
  "Cell" should "be created as formula cell when initialized with string starting with '='" in {
    Cell("=3+2") shouldBe a[FormulaCell]
  }

  it should "be created as a string cell normal text content" in {
    Cell("Normal text") shouldBe a[StringCell]
  }
  * 
  */
  
  test("test1") {
    
    val ss = SlideShow(
      Slide(
            title = "Slide 1",
            layout = SlideLayout(typ = enums.SlideLayout.Blank)
          ),
      Slide(
            title = "Slide 2",
            layout = SlideLayout(typ = enums.SlideLayout.TitleAndContent)
          ) 
    )
    ss.saveAsPptx("/home/tom/Desktop/workbook.pptx")
    
  }

}