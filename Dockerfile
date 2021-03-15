FROM ubuntu:latest as grantPermissions

COPY entrypoint.sh /
COPY cmd.sh /
RUN chmod +x /entrypoint.sh; \
    chmod +x /cmd.sh;

FROM jetty:9.4.38-jre11-slim

COPY target/payroll-groovy*.war /var/lib/jetty/webapps/ROOT.war
COPY --from=grantPermissions /entrypoint.sh /
COPY --from=grantPermissions /cmd.sh /

ENV JAVA_OPTIONS="-Xmx450mb"

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/cmd.sh"]