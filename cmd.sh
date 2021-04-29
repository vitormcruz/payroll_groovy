#!/bin/sh
sed -i "s/# jetty.http.port=8080/jetty.http.port=$PORT/" $JETTY_BASE/start.ini
java -jar /usr/local/jetty/start.jar
