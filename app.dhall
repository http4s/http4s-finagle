let http4sVersion = "0.21.33"

let finagleVersion = "22.7.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
