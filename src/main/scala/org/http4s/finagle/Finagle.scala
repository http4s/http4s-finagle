package org.http4s
package finagle

import org.http4s.client._
import cats.effect._
import cats.effect.std.Dispatcher
import cats.syntax.functor._
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Method, Version, Request => Req, Response => Resp}
import com.twitter.util.{Future, Return, Throw}
import com.twitter.io._
import cats.syntax.flatMap._
import cats.syntax.show._
import fs2.{Chunk, Stream}
import com.twitter.util.Promise
import cats.syntax.apply._
import com.twitter.finagle.http.Fields
import com.twitter.util.Base64StringEncoder
import java.nio.charset.StandardCharsets
import org.http4s.{Method => H4Method}
import org.typelevel.ci._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext

object Finagle {

  def mkClient[F[_]: Async](dest: String): Resource[F, Client[F]] =
    mkClient(Http.newService(dest))

  def mkClient[F[_]: Async](svc: Service[Req, Resp]): Resource[F, Client[F]] =
    Resource
      .make(allocate(svc)) { _ =>
        Async[F].delay(())
      }
  def mkService[F[_]: Async](route: HttpApp[F])(implicit ec: ExecutionContext): Resource[F, Service[Req, Resp]] =
    Dispatcher.parallel[F] map { dispatcher =>
      (req: Req) => toFuture(dispatcher, route.local(toHttp4sReq[F]).flatMapF(fromHttp4sResponse[F]).run(req))
    }

  private def allocate[F[_]: Async](svc: Service[Req, Resp]): F[Client[F]] =
    Async[F].delay(Client[F] { req =>
      Resource
        .eval(for {
          freq <- fromHttp4sReq(req)
          resp <- toF(svc(freq))
        } yield resp)
        .map(toHttp4sResp)
    })

  private def toHttp4sReq[F[_]](req: Req): Request[F] = {
    val method = H4Method.fromString(req.method.name).getOrElse(H4Method.GET)
    val uri = Uri.unsafeFromString(req.uri)
    val headers = Headers(req.headerMap.toList.map { case (name, value) => Header.Raw(CIString(name), value) })
    val body = toStream[F](req.content)
    val version = HttpVersion
      .fromVersion(req.version.major, req.version.minor)
      .getOrElse(HttpVersion.`HTTP/1.1`)
    Request(method, uri, version, headers, body)
  }

  private def fromHttp4sResponse[F[_]: Async](resp: Response[F]): F[Resp] = {
    import com.twitter.finagle.http.Status
    val status = Status(resp.status.code)
    val headers = resp.headers.headers.map(h => (h.name.show, h.value))
    val finagleResp = Resp(status)
    headers.foreach{case (k, v) => finagleResp.headerMap.add(k,v)}
    val writeBody = if (resp.isChunked) {
      finagleResp.setChunked(true)
      Concurrent[F].start(streamBody(resp.body, finagleResp.writer).compile.drain).void
    } else {
      resp
        .as[Array[Byte]]
        .map { Buf.ByteArray.Owned(_) }
        .map(finagleResp.content = _)
        .void
    }
    writeBody.as(finagleResp)
  }

  def fromHttp4sReq[F[_]: Async](req: Request[F]): F[Req] = {
    val method = Method(req.method.name)
    val version = Version(req.httpVersion.major, req.httpVersion.minor)
    val request = Req(version, method, req.uri.toString)
    req.uri.host.foreach(uri => request.headerMap.add(Fields.Host, uri.value))
    req.uri.userInfo.foreach{user =>
      val repr = user.username ++ user.password.fold("")(":" ++ _)
      val auth = "Basic " + Base64StringEncoder.encode(repr.getBytes(StandardCharsets.UTF_8))
      request.headerMap.add(Fields.Authorization, auth)
    }
    req.headers.headers.foreach { h=>
        request.headerMap.add(h.name.show, h.value)
    }

    if (req.isChunked) {
      request.headerMap.remove(Fields.TransferEncoding)
      request.setChunked(true)
      Spawn[F].start(streamBody(req.body, request.writer).compile.drain) *> Async[F].delay(request)
    } else {
      req.as[Array[Byte]].map { b =>
        if(b.nonEmpty) {
          val content = Buf.ByteArray.Owned(b)
          request.content = content
          request.contentLength = content.length.longValue()
        }
      } *> Async[F].delay(request)
    }
  }

  private def streamBody[F[_]: Async](
      body: Stream[F, Byte],
      writer: Writer[Buf]): Stream[F, Unit] = {
    import com.twitter.finagle.http.Chunk
    body.chunks.map(a => Chunk.fromByteArray(a.toArray).content).evalMap { a =>
      toF(writer.write(a))
    } ++ Stream.eval { toF(writer.close()) }
  }

  private def toStream[F[_]](buf: Buf): Stream[F, Byte] =
    Stream.chunk[F, Byte](Chunk.array(Buf.ByteArray.Owned.extract(buf)))

  private def toHttp4sResp[F[_]](resp: Resp): Response[F] = {
    Status.fromInt(resp.status.code) match {
      case Right(status) =>
        Response[F](
          status
        ).withHeaders(Headers(resp.headerMap.toList.map { case (name, value) => Header.Raw(CIString(name), value) }))
          .withEntity(toStream[F](resp.content))
      case Left(parseFailure) => parseFailure.toHttpResponse(HttpVersion(resp.version.major, resp.version.minor))
    }
  }

  private def toF[F[_]: Async, A](f: Future[A]): F[A] = Async[F].async_ { cb =>
      f.respond {
        case Return(value) =>
          cb(Right(value))
        case Throw(exception) =>
          cb(Left(exception))
      }
      ()
    }

  private def toFuture[F[_], A](dispatcher: Dispatcher[F], f: F[A])(implicit ec: ExecutionContext): Future[A] = {
      val promise: Promise[A] = Promise()
      dispatcher.unsafeToFuture(f).onComplete {
        case Success(value) => promise.setValue(value)
        case Failure(exception) => promise.setException(exception)
      }
    promise
  }
}
