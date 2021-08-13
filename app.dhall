let http4sVersion = "0.21.26"

let finagleVersion = "21.8.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
