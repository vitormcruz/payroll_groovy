FROM jetty:9.4.38-jre11-slim

COPY target/payroll-groovy*.war /var/lib/jetty/webapps/ROOT.war
COPY entrypoint.sh /
COPY cmd.sh /

RUN export JAVA_OPTIONS="-Xmx512mb"

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/cmd.sh"]