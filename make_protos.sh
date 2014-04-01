#!/bin/sh
SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export SRC_DIR=$SCRIPT_PATH/src/main/java

for f in `find $SCRIPT_PATH -name "*.proto"`; do
    protoc -I=$SCRIPT_PATH --java_out=$SCRIPT_PATH/src/main/java  $f
done
