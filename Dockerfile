FROM jetty:9.4.38-jre11-slim

RUN export JAVA_OPTIONS="-Xmx512mb"

COPY target/payroll-groovy*.war /var/lib/jetty/webapps/ROOT.war

