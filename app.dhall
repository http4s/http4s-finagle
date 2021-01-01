let http4sVersion = "0.21.15"

let finagleVersion = "20.10.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
