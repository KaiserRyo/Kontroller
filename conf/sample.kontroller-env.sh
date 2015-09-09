#!/bin/bash

JAVA=`which java`
BASEDIR=/opt/kontroller
BINDIR="$BASEDIR/bin"
LIBDIR="$BASEDIR/lib"
LOGDIR="/var/log/kontroller"
CONFIGDIR="$BASEDIR/config"
CONFIG_FILE="$CONFIGDIR/kontroller.yaml"
EXECUTABLE_JAR="$LIBDIR/kontroller-0.0.3-SNAPSHOT.jar"
LOG4JPROPERTIES=$CONFIGDIR/log4j.properties
PIDBASE=/var/run/kontroller
JMXPORT=9519
KONTROLLER_USER=kontroller

JAVA_OPTS=""
JAVA_OPTS="$JAVA_OPTS -server"
JAVA_OPTS="$JAVA_OPTS -Xms1G -Xmx1G"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly -XX:+CMSConcurrentMTEnabled -XX:+CMSScavengeBeforeRemark"
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=30"

JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution"
JAVA_OPTS="$JAVA_OPTS -Xloggc:$LOGDIR/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=10M"

JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JMXPORT"

JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=file:$LOG4JPROPERTIES"

JAVA_OPTS="$JAVA_OPTS -Dkontroller.logs.dir=$LOGDIR"
JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$LIBDIR"