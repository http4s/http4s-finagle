let http4sVersion = "0.21.22"

let finagleVersion = "21.3.0"

in  { version = "${http4sVersion}-${finagleVersion}"
    , http4sVersion
    , finagleVersion
    }
