let http4sVersion = "0.23.15"

let finagleVersion = "22.3.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
