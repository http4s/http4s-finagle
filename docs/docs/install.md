---
layout: doc-page
title: "Installation"
---

The latest version currently is 
[![](https://index.scala-lang.org/http4s/http4s-finagle/latest.svg?v=1)](https://index.scala-lang.org/http4s/http4s-finagle)

http4s-finagle is available for Scala 2.12, 2.13 and dotty, please refer to https://index.scala-lang.org/http4s/http4s-finagle for detail.

In general if your `http4s` version is `0.21.6`
simply set `http4s-finagle` to `0.21.6+`
```scala
libraryDependencies += "org.http4s" %% "http4s-finagle" % "0.21.6+"
```

http4s-finagle version is a combination of both version of http4s and finagle, finagle is pretty stable and monthly release,
so you probably don't want to fix the finagle version.

```
libraryDependencies += "org.http4s" %% "http4s-finagle" % "0.21.4-20.4.1-0"
                                                           ^      ^      ^
                                                      http4s   fingale   patch
```
