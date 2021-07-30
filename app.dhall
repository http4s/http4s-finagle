let http4sVersion = "0.22.1"

let finagleVersion = "21.6.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
