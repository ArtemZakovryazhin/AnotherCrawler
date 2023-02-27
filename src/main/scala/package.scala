import spray.json.{DefaultJsonProtocol, RootJsonFormat}

package object Crawler extends DefaultJsonProtocol {
  trait CrawlerJsonProtocol {
    implicit val uriTitleFormat: RootJsonFormat[UriTitle] = jsonFormat2(UriTitle)
  }
}