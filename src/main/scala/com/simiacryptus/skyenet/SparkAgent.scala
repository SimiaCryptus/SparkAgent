package com.simiacryptus.skyenet

import com.simiacryptus.skyenet.heart.ScalaLocalInterpreter.getTypeTagTuple
import com.simiacryptus.skyenet.util.AbbrevBlacklistYamlDescriber
import org.apache.spark.sql.SparkSession
import org.jfree.chart.ChartFactory
import org.jfree.graphics2d.svg.{SVGGraphics2D, SVGUtils}

import java.awt.Desktop
import java.io.File
import java.net.URI

//noinspection ScalaUnusedSymbol
object SparkAgent {

  lazy val sparkSession: SparkSession = {
    val sparkSession = SparkSession.builder()
      .appName("LocalTestingApp")
      .master("local[*]")
      .config("spark.executor.memory", "2g")
      .config("spark.driver.memory", "2g")
      .config("spark.cores.max", "2")
      .getOrCreate()
    org.apache.spark.sql.SparkSession.setDefaultSession(sparkSession)
    sparkSession
  }

  class SparkClients(session: SparkSession) {

    def getSparkSession(): SparkSession = session

    def getSparkContext() = getSparkSession().sparkContext

    def getSqlContext() = getSparkSession().sqlContext

    def getChartFactory(): ChartFactory = new ChartFactory() {}

    def getSvgGraphics(width: Int, height: Int) = new SVGGraphics2D(width, height)

    def outputSVG(graphics2D: SVGGraphics2D, filename: String) = {
      val width = graphics2D.getWidth
      val height = graphics2D.getHeight
      val svgElement: String = graphics2D.getSVGElement
      System.out.println(
        s"""<svg width="$width" height="$height" viewBox="0 0 $width $height" xmlns="http://www.w3.org/2000/svg">
           |$svgElement
           |</svg>
           |""".stripMargin)
    }

  }


  class HttpUtil {
    def client() = org.apache.http.impl.client.HttpClients.createDefault()

  }

  def main(args: Array[String]): Unit = {
    OutputInterceptor.setupInterceptor()
    val port = 8083
    val server = new SkyenetSessionServerScala(
      port = port,
      skyenetSimpleSessionServer = "SparkAgent",
      model = "gpt-4-0314",
      describer = new AbbrevBlacklistYamlDescriber(
        "com.amazonaws",
        "org.apache",
        "org.jfree",
        "java.lang"
      )
    ).start(port)
    Desktop.getDesktop.browse(URI.create(s"http://localhost:$port"))
    server.join()
  }

  def scalaApi = List(
    getTypeTagTuple("spark", new SparkClients(sparkSession)),
    getTypeTagTuple("client", new HttpUtil())
  )

}