#!/bin/sh
set -e

# Wait for application to be ready
until curl -fk https://localhost:8443/actuator/health > /dev/null 2>&1; do
    echo "Waiting for application to start..."
    sleep 5
done

echo "Application is ready, registering with Consul..."

# Get container IP
IP=$(hostname -i | awk '{print $1}')

# Register service with Consul
curl -X PUT "http://${CONSUL_HOST}:${CONSUL_PORT}/v1/agent/service/register" \
    -H "Content-Type: application/json" \
    -d "{
        \"ID\": \"${SERVICE_NAME}-${INSTANCE_ID}\",
        \"Name\": \"${SERVICE_NAME}\",
        \"Address\": \"${IP}\",
        \"Port\": 8443,
        \"Tags\": [\"${SERVICE_NAME}\", \"wildfly\", \"https\"],
        \"Check\": {
            \"HTTP\": \"http://${IP}:8080/actuator/health\",
            \"Interval\": \"15s\",
            \"Timeout\": \"5s\",
            \"DeregisterCriticalServiceAfter\": \"1m\"
        }
    }"

echo "Successfully registered ${SERVICE_NAME}-${INSTANCE_ID} with Consul"

# Keep container running by tailing nothing (entrypoint will handle the actual process)
wait
