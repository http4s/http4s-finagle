---
layout: doc-page
title: "Why Finagle x Http4s?"
---

Finagle is an extensible RPC system that Twitter running on, there are bunch of
production ready things to use, without needing particular setup, batteries included such as:

## Zipkin distributed tracing
So if all your microservices are running on Finagle, it is pretty much 0 config to get Zipkin
just works.
![](https://zipkin.io/public/img/web-screenshot.png)
you can query a request's journey through all your microservices by just query it's trace id,
interesting data will be summarized for you, such as the percentage of time spent in which service, and whether or not operations failed.

## Metrics
Finagle will automatically generate [metrics](https://twitter.github.io/finagle/guide/Metrics.html) for server and client when you enable them with
```scala
.withHttpStats
```
There are some adapter ready to use to expose to different format, such as [Prometheus](https://samstarling.co.uk/projects/finagle-prometheus/)

## Circuit Breaker x Load Balancer x Retry
- [Circuit Breaker](https://twitter.github.io/finagle/guide/Clients.html#circuit-breaking)
- [Load Balancer](https://twitter.github.io/finagle/guide/ApertureLoadBalancers.html)
- [Retry](https://twitter.github.io/finagle/guide/Clients.html#retries)

Since Finagle is RPC system, it is very easy to detect error accurately, you can also apply [custom
classifier](https://twitter.github.io/finagle/guide/Clients.html#custom-classifiers) to identity different kinds of error

## Twitter Server
If we run Finagle Service on a [Twitter Server](https://twitter.github.io/twitter-server/), then we will get

- Logging with dynamo log level control
- flag and feature toggle
- a Admin portal that can monitor metrics and histograms

## Finagle x Http4s

The pain point is when there are lot of existing Finagle RPC services running on production already, and
when I introduced Http4s service, I need to spend too many time to make Zipkin, metrics, circuit breaker work the
same way with the existing services, but if I give up new service will be couple with Finagle/Finatra dsl forever.

Now with http4s-finagle, we can actually take advantage of both, create business with Http4s, and run them on Finagle to keep Devops practice consistent.
