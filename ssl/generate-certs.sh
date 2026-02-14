#!/bin/bash

# Скрипт для генерации самоподписанных SSL сертификатов
# Использование: ./generate-certs.sh [SERVER_IP_OR_DOMAIN]
# Пример: ./generate-certs.sh 192.168.1.100
# Пример: ./generate-certs.sh myserver.example.com

SSL_DIR="$(dirname "$0")"
SERVER_HOST="${1:-localhost}"

echo "Генерация SSL сертификатов для: $SERVER_HOST"

# Определяем, является ли SERVER_HOST IP-адресом
if [[ $SERVER_HOST =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    # Это IP-адрес
    SAN_EXTRA="IP:$SERVER_HOST"
    echo "Обнаружен IP-адрес: $SERVER_HOST"
else
    # Это доменное имя
    SAN_EXTRA="DNS:$SERVER_HOST"
    echo "Обнаружен домен: $SERVER_HOST"
fi

# Генерация приватного ключа и сертификата
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout "$SSL_DIR/server.key" \
    -out "$SSL_DIR/server.crt" \
    -subj "/C=RU/ST=Moscow/L=Moscow/O=SOA Lab/OU=Development/CN=$SERVER_HOST" \
    -addext "subjectAltName=DNS:localhost,DNS:city-service,DNS:genocide-service,DNS:frontend,$SAN_EXTRA,IP:127.0.0.1"

# Создание PKCS12 keystore для Java сервисов
openssl pkcs12 -export -in "$SSL_DIR/server.crt" -inkey "$SSL_DIR/server.key" \
    -out "$SSL_DIR/keystore.p12" -name tomcat -password pass:changeit

# Создание truststore с сертификатом сервера (для клиентов)
keytool -import -trustcacerts -alias server-cert \
    -file "$SSL_DIR/server.crt" \
    -keystore "$SSL_DIR/truststore.p12" \
    -storetype PKCS12 \
    -storepass changeit \
    -noprompt

echo "SSL сертификаты сгенерированы в директории $SSL_DIR"
echo "  - server.key: приватный ключ"
echo "  - server.crt: сертификат"
echo "  - keystore.p12: Java keystore (пароль: changeit)"
echo "  - truststore.p12: Java truststore для клиентов (пароль: changeit)"
