package com.simiacryptus.skyenet

import com.simiacryptus.skyenet.SparkAgent.{scalaApi, sparkSession}
import com.simiacryptus.skyenet.heart.ScalaLocalInterpreter
import com.simiacryptus.skyenet.util.AbbrevBlacklistYamlDescriber
import com.simiacryptus.skyenet.body.SkyenetSessionServer

import java.util

class SkyenetSessionServerScala(
                                 port: Int,
                                 skyenetSimpleSessionServer: String,
                                 model: String = "gpt-3.5-turbo",
                                 describer: AbbrevBlacklistYamlDescriber,
                                 oauthPath: String = null // new File(new File(System.getProperty("user.home")), "client_secret_google_oauth.json").getAbsolutePath
) extends SkyenetSessionServer(
  skyenetSimpleSessionServer,
  describer,
  oauthPath,
  false,
  "simpleSession",
  5,
  4000,
  s"http://localhost:$port",
  model,
  true
) {

  override def toString(e: Throwable): String = {
    Option(e.getMessage).filter(!_.trim.isEmpty).getOrElse(e.toString)
  }

  override def heart(hands: java.util.Map[String, Object]): Heart = {
    sparkSession.synchronized {
      new ScalaLocalInterpreter(
        scalaApi: _*
      )
    }
  }

  override def hands(): util.Map[String, AnyRef] = {
    val map = new util.HashMap[String, AnyRef]()
    sparkSession.synchronized {
      scalaApi.foreach {
        case (k, v, t) => map.put(k, v)
      }
    }
    map
  }

}
