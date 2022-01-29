let http4sVersion = "0.23.9"

let finagleVersion = "21.12.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
