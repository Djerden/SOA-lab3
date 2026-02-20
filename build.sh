#!/bin/bash

# Build script for Lab2 microservices

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== Генерация SSL сертификатов ==="
cd ssl
./generate-certs.sh
cd ..

echo "=== Сборка City Service ==="
cd city-service
./gradlew clean build -x test
cd ..

echo "=== Сборка Genocide Service ==="
cd genocide-service
./gradlew clean build -x test
cd ..

echo "=== Сборка Docker образов ==="
docker compose build

echo "=== Готово! ==="
echo "Для запуска используйте: docker compose up -d"
echo "HAProxy Stats: http://localhost:8404/stats"
echo "Consul UI: http://localhost:8500"
