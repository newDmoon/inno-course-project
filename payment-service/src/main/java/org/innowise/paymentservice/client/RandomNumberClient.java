package org.innowise.paymentservice.client;

import lombok.RequiredArgsConstructor;
import org.innowise.paymentservice.util.ApplicationConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RandomNumberClient {
    private final RestTemplate restTemplate;

    @Value("${external.random-number.base-url}")
    private String baseUrl;

    public int getRandomInt(int min, int max) {
        String url = String.format(ApplicationConstant.GENERATE_NUMBER_QUERY, baseUrl, min, max);

        try {
            int[] response = restTemplate.getForObject(url, int[].class);

            if (response == null || response.length == 0) {
                throw new IllegalStateException("RandomNumber API returned empty response");
            }

            return response[0];

        } catch (RestClientException ex) {
            throw new IllegalStateException("Failed to call RandomNumber API", ex);
        }
    }
}
