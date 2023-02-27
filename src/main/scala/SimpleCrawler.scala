import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import scala.concurrent.Future
import scala.io.StdIn
import Crawler._

object SimpleCrawler extends CrawlerJsonProtocol with SprayJsonSupport {

  implicit val system: ActorSystem = ActorSystem("AsWeeCan")
  import system.dispatcher

  def getTitle(url: String): Future[UriTitle] = {
    Http().singleRequest(HttpRequest(uri = url)).flatMap{ response =>
      response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map{d =>
        val title = Option(d.utf8String.mkString.split("</title>").head
          .split("<title>").tail.mkString)
        UriTitle(url, title)
      }
    }
  }

  def getTitle(uriList: List[String]): Future[List[UriTitle]] = {
    Future.sequence {
      uriList.map{uri =>
        getTitle(uri).recover{
          case e: Exception => UriTitle(uri, None)
        }
      }
    }
  }

  val getTitleRoute: Route = {
    pathPrefix ("gimmeTitles") {
      get {
        path("get") {//задаем список УРЛ-ов как параметры crawlUrl
          parameters("crawlUrl".as[String].repeated) { urls =>
            urls.toList.map(_.trim).filter(_.nonEmpty) match {
              case Nil => complete("no single crawlUrl parameter specified")
              case urls => complete(getTitle(urls.reverse))
            }
          }
        }
      }~
        post { //принимаем список УРЛ-ов (просто список, каждый УРЛ с новой строки)
          extractDataBytes { data =>
            val text = data.runFold[String]("") { (string, i) => string + i.utf8String }
            onSuccess(text) { t =>
              val rawTextAsList = t.mkString.split("\\n").map(_.trim).toList
              complete(getTitle(rawTextAsList))
            }
          }
        }
    }
  }

  def main(args: Array[String]): Unit = {
    val bindingFuture = Http().newServerAt("localhost", 8088).bind(getTitleRoute)
    println(s"server now online. hit http://localhost:8088/gimmeTitles with POST request " +
      s"\nor with GET request with parameters crawlUrl" +
      s"\nPress RETURN to stop it")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }


}
