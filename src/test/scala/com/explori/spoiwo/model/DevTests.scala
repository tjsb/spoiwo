package com.explori.spoiwo.model

import org.scalatest.{FunSuite, Matchers}

import chart._
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
            Chart(
              title = "Test Chart",
              data = Seq(
                  BarChartData(
                    categoryAxis = CategoryAxis(
                                    title = "Languages", 
                                    values = Seq("English", "Russian", "Spanish")
                                    ),
                    valueAxes = Seq(
                        ValueAxis(
                            title = "Countries,speakers", 
                            data = Seq(
                                SeriesData(
                                    category = "countries", 
                                    values = List(13.00, 21.00, 7.00)
                                ),
                                SeriesData(
                                    category = "speakers", 
                                    values = List(23.00, 19.00, 46.00)
                                )
                           )
                       )
                     )
                  )
                )
            )
          ) 
    )
    ss.saveAsPptx("/home/tom/Desktop/workbook.pptx")
    
  }

}