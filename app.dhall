let http4sVersion = "0.21.16"

let finagleVersion = "21.1.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
