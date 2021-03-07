FROM jetty:9.4.38-jre11-slim

COPY target/payroll-groovy*.war /var/lib/jetty/webapps/ROOT.war

