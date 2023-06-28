let http4sVersion = "0.23.21"

let finagleVersion = "22.12.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
