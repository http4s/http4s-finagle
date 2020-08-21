package org.http4s.finagle

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.http4s.multipart._
import org.http4s._
import Uri._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.implicits._
import munit._
import cats.implicits._
import scala.concurrent.ExecutionContext
import client._
import cats.effect._
import scala.concurrent.duration._
import fs2._
import org.scalacheck.Prop._

class FinagleSpec extends munit.FunSuite with munit.ScalaCheckSuite {
  implicit val context: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  val service = Finagle.mkService{HttpRoutes.of[IO] {
    case req @ _ -> Root / "echo" => Ok(req.as[String])
    case GET -> Root / "simple" => Ok("simple path")
    case req @ POST -> Root / "chunked" => Response[IO](Ok)
          .withEntity(Stream.emits(req.as[String].unsafeRunSync.toSeq.map(_.toString)).covary[IO])
        .pure[IO]
    case GET -> Root / "delayed" => timer.sleep(1.second) *>
      Ok("delayed path")
    case GET -> Root / "no-content" => NoContent()
    case GET -> Root / "not-found" => NotFound("not found")
    case GET -> Root / "empty-not-found"  => NotFound()
    case GET -> Root / "internal-error" => InternalServerError()
  }.orNotFound}

  var client: (Client[IO], IO[Unit]) = null
  var server: com.twitter.finagle.ListeningServer = null
  override def beforeAll(): Unit = {
    client = Finagle.mkClient[IO]("localhost:8080").allocated[IO, Client[IO]].unsafeRunSync
    server = com.twitter.finagle.Http.serve(":8080", service)
    ()
  }

  override def afterAll():Unit = {
    server.close()
    client._2.unsafeRunSync()
    ()
  }
  val localhost = unsafeFromString("http://localhost:8080")

  test("GET") {
    val reqs = List(localhost / "simple", localhost / "delayed", localhost / "no-content")
    assertEquals(
      reqs.parTraverse(client._1.expect[String](_)).unsafeRunSync(),
      List("simple path", "delayed path", "")
    )
  }

  property("GET stream body") { forAll {(body: String)=>
      assertEquals(
        client._1.expect[String](POST(body, localhost / "chunked")).unsafeRunSync(),
        body
      )
  }}

  test("POST empty") {
    assertEquals(
      client._1.expect[String](POST(localhost / "echo")).unsafeRunSync(),
      ""
    )
  }

  test("GET not found") {
    assertEquals(
      client._1.status(GET(localhost / "not-found")).unsafeRunSync(),
      Status.NotFound
    )
    assertEquals(
      client._1.status(GET(localhost / "empty-not-found")).unsafeRunSync(),
      Status.NotFound
    )
  }

  test("GET 500") {
    assertEquals(
      client._1.status(GET(localhost / "internal-error")).unsafeRunSync(),
      Status.InternalServerError
    )
  }

  property("POST normal body") { forAll {(body: String)=>
      assertEquals(
        client._1.expect[String](POST(body, localhost / "echo")).unsafeRunSync(),
        body
      )
  }}

  property("POST chunked body") {forAll {(body: String)=>
    assertEquals(
        client._1.expect[String](POST(Stream(body).covary[IO], localhost / "echo")).unsafeRunSync(),
        body
      )
  }}

  property("POST multipart form") {
    forAll {(name: String, value: String)=>
      val multipart = Multipart[IO](Vector(Part.formData(name, value)))
      val req = POST(multipart, localhost / "echo").map(_.withHeaders(multipart.headers))
      assert(
        client._1.expect[String](req).unsafeRunSync().contains(value) == true
      )
    }
  }
  implicit val arbMethod:Arbitrary[Method] = Arbitrary{
    Gen.oneOf(Method.all).suchThat(!List(Method.CONNECT).contains(_))
  }
  implicit val arbVersion: Arbitrary[HttpVersion] = Arbitrary{ Gen.oneOf(List(HttpVersion.`HTTP/1.0`,
    HttpVersion.`HTTP/1.1`,
    HttpVersion.`HTTP/2.0`
  )) }

  property("arbitrary Methods x Versions x Body") {
    forAll {(method: Method, body: String, version: HttpVersion) =>
      val bodyUtf8Bytes = body.getBytes("UTF-8")
      val bodyUtf8 = new String(bodyUtf8Bytes, "UTF-8")

      val req = Request[IO](
        method = method,
        uri = localhost /"echo" ,
        httpVersion = version,
        body = Stream.emits(bodyUtf8Bytes).covary[IO]
      )
      method match {
        case Method.HEAD => assertEquals(
          client._1.status(req).unsafeRunSync,
          Ok
        )
        case _ => assertEquals(
          client._1.expect[String](req).unsafeRunSync,
          bodyUtf8
        )
      }
    }
  }
}
