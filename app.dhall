let http4sVersion = "0.21.31"

let finagleVersion = "22.2.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
