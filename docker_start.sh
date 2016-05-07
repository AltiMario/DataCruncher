#!/bin/bash

export JAVA_OPTS="-Dsqlhost=jdbc:mysql://mysql/datacruncher -Dnosqlhost=mongo"

/opt/tomcat/bin/catalina.sh run