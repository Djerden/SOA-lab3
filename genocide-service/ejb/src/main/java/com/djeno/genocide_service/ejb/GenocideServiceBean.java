package com.djeno.genocide_service.ejb;

import com.djeno.genocide_service.ejb.dto.CityDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless EJB реализация бизнес-логики GenocideService.
 * Содержит всю логику для операций с населением городов.
 */
@Stateless
public class GenocideServiceBean implements GenocideServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(GenocideServiceBean.class.getName());

    @Resource(name = "cityServiceUrl")
    private String cityServiceUrl;

    @Resource(name = "sslTrustStorePath")
    private String trustStorePath;

    @Resource(name = "sslTrustStorePassword")
    private String trustStorePassword;

    private HttpClient httpClient;
    private Jsonb jsonb;
    private String baseUrl;

    @PostConstruct
    public void init() {
        try {
            // Используем переменные окружения если @Resource не работает
            baseUrl = cityServiceUrl != null ? cityServiceUrl : System.getenv("CITY_SERVICE_URL");
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "https://city-service:8443/";
            }
            
            String trustPath = trustStorePath != null ? trustStorePath : System.getenv("SSL_TRUSTSTORE");
            String trustPass = trustStorePassword != null ? trustStorePassword : System.getenv("SSL_TRUSTSTORE_PASSWORD");
            if (trustPass == null) trustPass = "changeit";

            SSLContext sslContext = createSSLContext(trustPath, trustPass);
            
            httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            
            jsonb = JsonbBuilder.create();
            
            LOGGER.info("GenocideServiceBean initialized with cityServiceUrl: " + baseUrl);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize GenocideServiceBean", e);
            throw new RuntimeException("Failed to initialize EJB", e);
        }
    }

    private SSLContext createSSLContext(String trustStorePath, String trustStorePassword) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        
        if (trustStorePath != null && !trustStorePath.isEmpty()) {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (InputStream trustStoreStream = new FileInputStream(trustStorePath)) {
                trustStore.load(trustStoreStream, trustStorePassword.toCharArray());
            }
            
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            
            sslContext.init(null, tmf.getTrustManagers(), null);
        } else {
            // Использовать дефолтный trust manager (accept all for development)
            sslContext.init(null, new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            }, new java.security.SecureRandom());
        }
        
        return sslContext;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void killPopulationOfCity(int cityId) throws CityNotFoundException {
        String url = baseUrl + "cities/" + cityId + "/kill-population";
        LOGGER.info("Killing population of city: " + cityId);
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                throw new CityNotFoundException("City with id '" + cityId + "' not found");
            }
            
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to kill population: HTTP " + response.statusCode());
            }
            
            LOGGER.info("Successfully killed population of city: " + cityId);
        } catch (CityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error killing population of city: " + cityId, e);
            throw new RuntimeException("Failed to kill population", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int movePopulationToPoorestCity(int cityId) throws CityNotFoundException {
        LOGGER.info("Moving population from city: " + cityId);
        
        try {
            // 1. Получить исходный город
            CityDTO sourceCity = getCity(cityId);
            
            // 2. Найти самый бедный город
            CityDTO poorestCity = getPoorestCity(cityId, sourceCity.getStandardOfLiving());
            
            if (poorestCity == null) {
                LOGGER.info("No suitable city found for relocation, keeping population in city: " + cityId);
                return cityId;
            }
            
            // 3. Переселить население
            relocatePopulation(cityId, poorestCity.getId());
            
            LOGGER.info("Successfully moved population from city " + cityId + " to city " + poorestCity.getId());
            return poorestCity.getId();
        } catch (CityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error moving population from city: " + cityId, e);
            throw new RuntimeException("Failed to move population", e);
        }
    }

    private CityDTO getCity(int cityId) throws CityNotFoundException {
        String url = baseUrl + "cities/" + cityId;
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                throw new CityNotFoundException("City with id '" + cityId + "' not found");
            }
            
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to get city: HTTP " + response.statusCode());
            }
            
            return jsonb.fromJson(response.body(), CityDTO.class);
        } catch (CityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get city", e);
        }
    }

    private CityDTO getPoorestCity(int excludeId, String maxStandardOfLiving) {
        String url = baseUrl + "cities/poorest?excludeId=" + excludeId + "&maxStandardOfLiving=" + maxStandardOfLiving;
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                return null;
            }
            
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to get poorest city: HTTP " + response.statusCode());
            }
            
            return jsonb.fromJson(response.body(), CityDTO.class);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get poorest city", e);
            return null;
        }
    }

    private void relocatePopulation(int fromCityId, int toCityId) throws CityNotFoundException {
        String url = baseUrl + "cities/" + fromCityId + "/relocate-to/" + toCityId;
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 404) {
                throw new CityNotFoundException("City not found during relocation");
            }
            
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to relocate population: HTTP " + response.statusCode());
            }
        } catch (CityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to relocate population", e);
        }
    }
}
