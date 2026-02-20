FROM haproxy:2.9-alpine

# Install consul-template
ENV CONSUL_TEMPLATE_VERSION=0.37.4

USER root

RUN apk add --no-cache wget unzip bash curl && \
    wget -q "https://releases.hashicorp.com/consul-template/${CONSUL_TEMPLATE_VERSION}/consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip" -O /tmp/consul-template.zip && \
    unzip /tmp/consul-template.zip -d /usr/local/bin/ && \
    rm /tmp/consul-template.zip && \
    chmod +x /usr/local/bin/consul-template

# Create directories
RUN mkdir -p /etc/haproxy/templates /etc/consul-template /var/run/haproxy

# Copy HAProxy template
COPY haproxy.cfg.ctmpl /etc/haproxy/templates/haproxy.cfg.ctmpl

# Copy consul-template config
COPY consul-template.hcl /etc/consul-template/config.hcl

# Copy entrypoint
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8443 8444 8404

ENTRYPOINT ["/docker-entrypoint.sh"]
