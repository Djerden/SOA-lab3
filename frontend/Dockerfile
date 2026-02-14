# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build arguments for environment variables
ARG VITE_CITY_SERVICE_URL
ARG VITE_GENOCIDE_SERVICE_URL

# Build the app
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built assets
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create SSL directory
RUN mkdir -p /etc/nginx/ssl

# Expose ports
EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]
