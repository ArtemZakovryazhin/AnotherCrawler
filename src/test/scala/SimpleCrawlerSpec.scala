import SimpleCrawler.getTitleRoute
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import akka.util.ByteString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import Crawler._

class SimpleCrawlerSpec extends AnyWordSpec with Matchers with CrawlerJsonProtocol with SprayJsonSupport with ScalatestRouteTest {

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(5.seconds.dilated)

  "SimpleCrawler" should {
    "return list of JSONs {uri, title}" in {
      val wiki1 = ByteString("https://www.wikipedia.org\nhttps://www.google.com")

      Post("/gimmeTitles", wiki1) ~> getTitleRoute ~> check
      {
        responseAs[List[UriTitle]] shouldBe List(UriTitle("https://www.wikipedia.org", Some(value = "Wikipedia")), UriTitle("https://www.google.com", Some("Google")))
      }
    }

  }
  "SimpleCrawler with input from file" should {
    "return list of JSONs {uri, title}" in {
      val wikiS = Source.fromFile("src/test/scala/UriTestList")
      val wikiB = wikiS.mkString
      Post("/gimmeTitles", wikiB) ~> getTitleRoute ~> check {
        responseAs[List[UriTitle]] shouldBe List(UriTitle("https://www.wikipedia.org", Some(value = "Wikipedia")), UriTitle("https://www.google.com", Some("Google")))
      }
    }
  }
  "SimpleCrawler with GET method" should {
    "return list of JSONs {uri, title}" in {
      Get("/gimmeTitles/get?crawlUrl=https://www.wikipedia.org&crawlUrl=https://www.google.com&crawlUrl=https://yandex.ru") ~> getTitleRoute ~> check {
        responseAs[List[UriTitle]] shouldBe List(UriTitle("https://www.wikipedia.org", Some(value = "Wikipedia")), UriTitle("https://www.google.com", Some("Google")),  UriTitle("https://yandex.ru", Some("")))
      }
    }
  }
}
