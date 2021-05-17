let http4sVersion = "0.21.23"

let finagleVersion = "21.4.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
