#!/bin/bash
set -e

echo "Starting HAProxy with consul-template..."

# Wait for Consul to be available
echo "Waiting for Consul at ${CONSUL_ADDR:-consul:8500}..."
until curl -sf "http://${CONSUL_ADDR:-consul:8500}/v1/status/leader" > /dev/null 2>&1; do
    echo "Consul not ready, waiting..."
    sleep 2
done
echo "Consul is available!"

# Generate initial config from template
echo "Generating initial HAProxy config..."
consul-template \
    -consul-addr="${CONSUL_ADDR:-consul:8500}" \
    -template="/etc/haproxy/templates/haproxy.cfg.ctmpl:/usr/local/etc/haproxy/haproxy.cfg" \
    -once

# Validate initial config
echo "Validating HAProxy config..."
haproxy -c -f /usr/local/etc/haproxy/haproxy.cfg

# Start HAProxy in background and save PID
echo "Starting HAProxy..."
haproxy -W -f /usr/local/etc/haproxy/haproxy.cfg -p /var/run/haproxy/haproxy.pid &
HAPROXY_PID=$!

# Give HAProxy a moment to start
sleep 2

# Start consul-template to watch for changes
echo "Starting consul-template for dynamic updates..."
exec consul-template \
    -consul-addr="${CONSUL_ADDR:-consul:8500}" \
    -config="/etc/consul-template/config.hcl"
