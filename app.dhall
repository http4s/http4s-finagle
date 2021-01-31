let http4sVersion = "1.0.0-M10"

let finagleVersion = "21.1.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
