let http4sVersion = "0.21.21"

let finagleVersion = "21.2.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
