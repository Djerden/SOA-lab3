// Конфигурация API endpoints
// При локальной разработке используйте localhost
// При развертывании через docker-compose используются относительные пути через nginx proxy

const config = {
  // URL city-service (через nginx proxy: /api/city/)
  cityServiceUrl: import.meta.env.VITE_CITY_SERVICE_URL || '/api/city',
  
  // URL genocide-service (через nginx proxy: /api/genocide/)
  genocideServiceUrl: import.meta.env.VITE_GENOCIDE_SERVICE_URL || '/api/genocide',
};

export default config;
