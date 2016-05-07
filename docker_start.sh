#!/bin/bash

export JAVA_OPTS="-Dsqlhost=jdbc:mysql://$MYSQL_PORT_3306_TCP_ADDR:$MYSQL_PORT_3306_TCP_PORT/datacruncher -Dnosqlhost=localhost"

/opt/tomcat/bin/catalina.sh run