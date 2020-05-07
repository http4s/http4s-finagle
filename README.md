# Http4s Finagle

## Server

```scala
import org.http4s.finagle._
_
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
