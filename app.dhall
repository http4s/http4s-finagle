let http4sVersion = "0.23.4"

let finagleVersion = "21.6.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
