package org.innowise.paymentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innowise.paymentservice.exception.EmptyResourceException;
import org.innowise.paymentservice.exception.ExternalServiceException;
import org.innowise.paymentservice.model.PaymentStatus;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class RandomNumberClient {
    private final RestTemplate restTemplate;

    @Value("${external.random-number.base-url}")
    private String baseUrl;
    @Value("${external.random-number.min}")
    private int min;
    @Value("${external.random-number.max}")
    private int max;

    @Retryable(
            value = {RestClientException.class},
            maxAttemptsExpression = "${external.random-number.retry-attempts}",
            backoff = @Backoff(delayExpression = "${external.random-number.retry-delay}")
    )
    public int getRandomInt(int min, int max) {
        String url = String.format(ApplicationConstant.GENERATE_NUMBER_QUERY, baseUrl, min, max);

        try {
            int[] response = restTemplate.getForObject(url, int[].class);

            if (response == null || response.length == 0) {
                throw new EmptyResourceException("RandomNumber API returned empty response");
            }

            return response[0];

        } catch (RestClientException ex) {
            throw new ExternalServiceException("Failed to call RandomNumber API", ex);
        }
    }

    @Recover
    public int recoverRandomInt(ExternalServiceException ex, int min, int max) {
        log.warn("All retry attempts failed for getRandomInt. Activating recovery mode.", ex);
        return ApplicationConstant.DEFAULT_FAILED_RANDOM_VALUE;
    }

    public PaymentStatus determinePaymentStatus() {
        int number = getRandomInt(min, max);
        return (number % 2 == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
