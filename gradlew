#!/bin/sh
set -e
APP_HOME=$(pwd)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ -n "$JAVA_HOME" ]; then
 JAVACMD="$JAVA_HOME/bin/java"
else
 JAVACMD="java"
fi
exec "$JAVACMD" \
 -Xmx64m \
 -Xms64m \
 -classpath "$CLASSPATH" \
 org.gradle.wrapper.GradleWrapperMain "$@"
