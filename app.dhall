let http4sVersion = "0.22.15"

let finagleVersion = "22.4.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
