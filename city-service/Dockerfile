FROM quay.io/wildfly/wildfly:latest

# Environment variables
ENV WILDFLY_HOME /opt/jboss/wildfly
ENV DEPLOYMENT_DIR $WILDFLY_HOME/standalone/deployments

# Admin for management console
RUN $WILDFLY_HOME/bin/add-user.sh admin admin --silent

# Копируем keystore для SSL
COPY ssl/keystore.p12 $WILDFLY_HOME/standalone/configuration/keystore.p12

# Копируем CLI-скрипт для настройки SSL
COPY wildfly/configure-ssl.cli /opt/jboss/wildfly/configure-ssl.cli

# Выполняем CLI для настройки HTTPS
RUN $WILDFLY_HOME/bin/jboss-cli.sh --file=/opt/jboss/wildfly/configure-ssl.cli

# Copy Consul registration script
USER root
COPY register-consul.sh /opt/jboss/register-consul.sh
RUN chmod +x /opt/jboss/register-consul.sh
USER jboss

# WAR file
COPY build/libs/city-service-0.0.1-SNAPSHOT.war $DEPLOYMENT_DIR/

# Create marker file for auto-deployment
RUN touch $DEPLOYMENT_DIR/city-service.war.dodeploy

# HTTPS порт
EXPOSE 8443 8080 9990 8787

# Clean config history on start and run WildFly
CMD rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/* 2>/dev/null; \
    /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0