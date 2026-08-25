package org.example.pensionatapp.pensionat.customer.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class CustomerClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerClient.class);

    private final RestClient restClient;

    public CustomerClient(RestClient.Builder builder, @Value("${customer-service.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<CustomerDto> findByEmail(String email) {
        try {
            CustomerDto dto = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/customers/by-email")
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .body(CustomerDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to fetch customer by email {} from customer-service", email, e);
            throw new CustomerServiceUnavailableException("Kan inte nå kundtjänsten just nu");
        }
    }

    public Optional<CustomerDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            CustomerDto dto = restClient.get()
                    .uri("/api/customers/{id}", id)
                    .retrieve()
                    .body(CustomerDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to fetch customer by id {} from customer-service", id, e);
            throw new CustomerServiceUnavailableException("Kan inte nå kundtjänsten just nu");
        }
    }
}