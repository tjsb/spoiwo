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
            BarChart(
              title = "Test Chart",
              data = BarChartData(
                      categoryAxis = CategoryAxis(
                                    legend = "Languages", 
                                    values = Seq("English", "Russian", "Spanish")
                                    ),
                      yAxis = ValueAxis(
                            legend = "Num Exhibitors vs Num Visitors by language", 
                            data = Seq(
                                SeriesData(
                                    category = "Num Exhibitors", 
                                    values = Seq(13.00, 21.00, 7.00)
                                ),
                                SeriesData(
                                    category = "Num Visitors", 
                                    values = Seq(23.00, 19.00, 46.00)
                                )
                           )
                       )
                     )
                  
                
            )
          ),
       Slide(
            BarChart(
              title = "Test Chart 2",
              data = BarChartData(
                      categoryAxis = CategoryAxis(
                                    legend = "Languages", 
                                    values = Seq("French", "Finnish", "Swahili")
                                    ),
                      yAxis = ValueAxis(
                            legend = "Num Exhibitors vs Num Visitors by language", 
                            data = Seq(
                                SeriesData(
                                    category = "Num Exhibitors", 
                                    values = Seq(10.00, 9.00, 3.00)
                                ),
                                SeriesData(
                                    category = "Num Visitors", 
                                    values = Seq(34.00, 26.00, 23.00)
                                ),
                                SeriesData(
                                    category = "Num Nobodies", 
                                    values = Seq(5.00, 8.00, 4.00)
                                )
                           )
                       )
                     )
                  
                
            )
          )
    )
    ss.saveAsPptx("/home/tom/Desktop/workbook3.pptx")
    //ss.saveAsPptx("/home/tom/Desktop/workbook2.pptx.zip")
    
  }

}