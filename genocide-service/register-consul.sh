#!/bin/sh
set -e

# Wait for application to be ready
until curl -fk https://localhost:8443/actuator/health > /dev/null 2>&1; do
    echo "Waiting for application to start..."
    sleep 5
done

echo "Application is ready, registering with Consul..."

# Get container IP from /etc/hosts (Docker adds container IP as last entry)
# Filter out localhost entries and get IPv4 address
HOSTNAME_VAL=$(cat /etc/hostname)
IP=$(grep -E "^[0-9]+\." /etc/hosts | grep "$HOSTNAME_VAL" | awk '{print $1}' | head -1)
if [ -z "$IP" ]; then
  # Fallback: get last non-localhost IPv4 entry from /etc/hosts
  IP=$(grep -E "^[0-9]+\." /etc/hosts | grep -v "^127\." | tail -1 | awk '{print $1}')
fi

echo "Detected container IP: $IP"

# Register service with Consul
curl -X PUT "http://${CONSUL_HOST}:${CONSUL_PORT}/v1/agent/service/register" \
    -H "Content-Type: application/json" \
    -d "{
        \"ID\": \"${SERVICE_NAME}-${INSTANCE_ID}\",
        \"Name\": \"${SERVICE_NAME}\",
        \"Address\": \"${IP}\",
        \"Port\": 8080,
        \"Tags\": [\"${SERVICE_NAME}\", \"wildfly\", \"ejb\"],
        \"Check\": {
            \"HTTP\": \"http://${IP}:8080/actuator/health\",
            \"Interval\": \"15s\",
            \"Timeout\": \"5s\",
            \"DeregisterCriticalServiceAfter\": \"1m\"
        }
    }"

echo "Successfully registered ${SERVICE_NAME}-${INSTANCE_ID} with Consul"
