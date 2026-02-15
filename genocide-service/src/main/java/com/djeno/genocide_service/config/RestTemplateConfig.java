package com.djeno.genocide_service.config;

import java.io.File;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${ssl.trust-store:#{null}}")
    private String trustStorePath;

    @Value("${ssl.trust-store-password:changeit}")
    private String trustStorePassword;

    @Bean
    public RestTemplate restTemplate() throws Exception {
        SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();

        // Если truststore указан, используем его, иначе доверяем системным CA
        if (trustStorePath != null && !trustStorePath.isEmpty()) {
            File trustStoreFile = new File(trustStorePath);
            if (trustStoreFile.exists()) {
                sslContextBuilder.loadTrustMaterial(
                        trustStoreFile,
                        trustStorePassword.toCharArray()
                );
            }
        }

        SSLContext sslContext = sslContextBuilder.build();

        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .build();

        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
