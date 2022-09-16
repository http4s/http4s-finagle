let http4sVersion = "0.23.16"

let finagleVersion = "22.3.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
