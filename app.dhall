let http4sVersion = "0.21.24"

let finagleVersion = "21.5.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
