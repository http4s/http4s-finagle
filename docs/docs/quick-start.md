---
layout: doc-page
title: "Quick Started"
---

## Server

To run Http4s app on Finagle Http server, simply just use `Finagle.mkService` to adapt Http4s `HttpApp[F]` to Fingale `Service[Request, Response]`.

```scala
import org.http4s.finagle._
import com.twitter.finagle.Http

val http4sService: HttpApp[IO] = HttpRoutes.of[IO] {...}.orNotFound

val server = Http.server.serve(":8080", Finagle.mkService(http4sService))
Await.ready(server)
```

## Client
To make a Finagle Client is the other way around, we need to adapt Finagle `Service[Request, Response]` to Http4s `Client[IO]`:

```scala
import org.http4s.finagle._
import com.twitter.finagle.Http

val host = "blog.oyanglul.us"
Finagle.mkClient[IO](Http.client.withTls(host).newService(s"$host:443")).use {
  client: Client[IO] =>
  ...
}
```

### :warning:
A fundamental different from usual Http client is that finagle `Client[IO]` is actually a RPC Http client,
which means it cannot send arbitrary request to any host, the host endpoint is already set when created
this client. This machinism is very useful to identify endpoint healthy, load balancer, circuit breaker.
But a little bit weird when using:

```scala
val host = "blog.oyanglul.us"
Finagle.mkClient[IO](Http.client.withTls(host).newService(s"$host:443")).use {
  client: Client[IO] =>
  client.status(uri"https://twiter.com")
  client.status(uri"https://abc.com/blahblah")
}
```

- `client.status(uri"https://twiter.com")` will still send request to `https://blog.oyanglul.us`
- `client.status(uri"https://abc.com/blahblah")` will send request to `https://blog.oyanglul.us/blahblah`
