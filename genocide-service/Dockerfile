FROM quay.io/wildfly/wildfly:latest

# Environment variables
ENV WILDFLY_HOME /opt/jboss/wildfly
ENV DEPLOYMENT_DIR $WILDFLY_HOME/standalone/deployments

# Admin for management console
RUN $WILDFLY_HOME/bin/add-user.sh admin admin --silent

# Копируем keystore и truststore для SSL
COPY ssl/keystore.p12 $WILDFLY_HOME/standalone/configuration/keystore.p12
COPY ssl/truststore.p12 $WILDFLY_HOME/standalone/configuration/truststore.p12

# Копируем CLI-скрипт для настройки SSL и EJB пула
COPY wildfly/configure-wildfly.cli /opt/jboss/wildfly/configure-wildfly.cli

# Выполняем CLI для настройки WildFly
RUN $WILDFLY_HOME/bin/jboss-cli.sh --file=/opt/jboss/wildfly/configure-wildfly.cli

# EAR file (содержит EJB и WAR)
COPY ear/build/libs/genocide-service-0.0.1-SNAPSHOT.ear $DEPLOYMENT_DIR/genocide-service.ear

# Create marker file for auto-deployment
RUN touch $DEPLOYMENT_DIR/genocide-service.ear.dodeploy

# HTTPS порт
EXPOSE 8443 9990

# Clean config history on start and run WildFly
CMD rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/* 2>/dev/null; \
    /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
