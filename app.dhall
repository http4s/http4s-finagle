let http4sVersion = "0.21.11"

let finagleVersion = "20.10.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
