# Http4s Finagle

[![](https://index.scala-lang.org/http4s/http4s-finagle/latest.svg?v=1)](https://index.scala-lang.org/http4s/http4s-finagle)
[![Build](https://github.com/http4s/http4s-finagle/workflows/Build%20and%20Test/badge.svg)](https://github.com/http4s/http4s-finagle/actions?query=workflow%3A%22Build+and+Test%22)
[![codecov](https://codecov.io/gh/http4s/http4s-finagle/branch/master/graph/badge.svg)](https://codecov.io/gh/http4s/http4s-finagle)\
![Cats Friendly Badge](https://typelevel.org/cats/img/cats-badge-tiny.png) 

if your `http4s` version is `0.21.x`
simply set `http4s-finagle` to `0.21+`
```scala
libraryDependencies += "org.http4s" %% "http4s-finagle" % "0.21+"
```

if in any circumstance you need fix the finagle version(most cases not, finagle api is quite stable)
```
libraryDependencies += "org.http4s" %% "http4s-finagle" % "0.21.4-20.4.1-0"
                                                           ^      ^      ^
                                                      http4s   fingale   patch
```

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

Please refer to the [Test](src/test/scala/org/http4s/finagle/FinagleSpec.scala) which is fully functional server and client.
