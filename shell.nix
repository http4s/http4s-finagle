with import <nixpkgs> {};
mkShell {
  buildInputs = [
    (sbt.override {jre = jdk8;})
  ];
}
