package org.example.pensionatapp.pensionat.customer.client;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class CustomerClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerClient.class);

    private final RestClient restClient;

    public CustomerClient(RestClient.Builder builder, @Value("${customer-service.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        HttpServletRequest servletRequest = attributes.getRequest();
                        String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                        if (authHeader != null && !authHeader.isBlank()) {
                            request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
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