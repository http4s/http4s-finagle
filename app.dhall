let http4sVersion = "0.21.15"

let finagleVersion = "21.1.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
