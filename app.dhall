let http4sVersion = "0.21.29"

let finagleVersion = "21.9.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
