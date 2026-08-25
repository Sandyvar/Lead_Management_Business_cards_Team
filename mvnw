#!/bin/sh
# Maven wrapper script
MAVEN_VERSION="3.9.9"
MAVEN_ZIP="apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/$MAVEN_ZIP"
WRAPPER_DIR="$HOME/.m2/wrapper/dists/apache-maven-$MAVEN_VERSION"
MAVEN_HOME="$WRAPPER_DIR/apache-maven-$MAVEN_VERSION"

if [ ! -d "$MAVEN_HOME" ]; then
    mkdir -p "$WRAPPER_DIR"
    echo "[INFO] Downloading Maven $MAVEN_VERSION..."
    curl -fsSL "$MAVEN_URL" -o "$WRAPPER_DIR/$MAVEN_ZIP"
    unzip -q "$WRAPPER_DIR/$MAVEN_ZIP" -d "$WRAPPER_DIR"
    rm "$WRAPPER_DIR/$MAVEN_ZIP"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
