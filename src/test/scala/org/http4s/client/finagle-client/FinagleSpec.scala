package org.http4s.finagle

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.http4s.multipart._
import org.http4s._
import Uri._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.implicits._
import cats.implicits._
import scala.concurrent.ExecutionContext
import client._
import cats.effect._
import scala.concurrent.duration._
import fs2._
import org.scalacheck.Prop._

class FinagleSpec extends munit.FunSuite with munit.ScalaCheckSuite {
  implicit val context = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  val service = Finagle.mkService{HttpRoutes.of[IO] {
    case req @ _ -> Root / "echo" => Ok(req.as[String])
    case GET -> Root / "simple" => Ok("simple path")
    case GET -> Root / "chunked" => Response[IO](Ok)
          .withEntity(Stream.emits("chunk".toSeq.map(_.toString)).covary[IO])
        .pure[IO]
    case GET -> Root / "delayed" => timer.sleep(1.second) *>
      Ok("delayed path")
    case GET -> Root / "no-content" => NoContent()
    case GET -> Root / "not-found" => NotFound("not fount")
    case GET -> Root / "empty-not-found"  => NotFound()
    case GET -> Root / "internal-error" => InternalServerError()
  }.orNotFound}

  var client: (Client[IO], IO[Unit]) = null
  var server: com.twitter.finagle.ListeningServer = null
  override def beforeAll = {
    client = Finagle.mkClient[IO]("localhost:8080").allocated.unsafeRunSync()
    server = com.twitter.finagle.Http.serve(":8080", service)
  }

  override def afterAll = {
    server.close()
    client._2.unsafeRunSync()
  }
  val localhost = uri("http://localhost:8080")
  test("Repeat a simple request") {
    assertEquals(
      (0 to 10).map(_=>client._1.expect[String](localhost / "simple")).toList.parSequence.unsafeRunSync(),
      (0 to 10).map(_=>"simple path").toList
    )
  }

  test("POST empty") {
    assertEquals(
      client._1.expect[String](POST(localhost / "echo")).unsafeRunSync(),
      ""
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
    implicit val arbMethod = Arbitrary{ Gen.oneOf (Method.all) }
    implicit val arbVersion = Arbitrary{ Gen.oneOf(List(HttpVersion.`HTTP/1.0`,
      HttpVersion.`HTTP/1.1`,
      HttpVersion.`HTTP/2.0`
    )) }

  property("Methods x Versions") {
    forAll {(method: Method, body: String, version: HttpVersion) =>
      val req = Request[IO](
        method = method,
        uri = localhost /"echo" ,
        httpVersion = version,
        body = Stream.emits(body.getBytes()).covary[IO]
      )
      assertEquals(
        client._1.expect[String](req).unsafeRunSync,
        body
      )
    }
  }
}
