# Http4s Finagle

![Build and Test](https://github.com/http4s/http4s-finagle/workflows/Build%20and%20Test/badge.svg)

## Server

To run Http4s app on Finagle Http server, simply just use `Finagle.mkService` to adapt Http4s `HttpApp[F]` to Fingale `Service[Request, Response]`.

```scala
import org.http4s.finagle._

val http4sService: HttpApp[IO] = ???

val server = Http.server.serve(":8080", Finagle.mkService(http4sService))
Await.ready(server)
```

## Client

```scala
import org.http4s.finagle._
import com.twitter.finagle.Http

val host = "blog.oyanglul.us"
Finagle.mkClient[IO](Http.client.withTls(host).newService(s"$host:443")).use {
  client: Client[IO] =>
  ...
}
```

Please refer to the [Test](src/test/scala/org/http4s/client/finagle-client/FinagleSpec.scala) which is fully functional server and client.
