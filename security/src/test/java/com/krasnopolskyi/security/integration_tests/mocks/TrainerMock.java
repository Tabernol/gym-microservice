package com.krasnopolskyi.security.integration_tests.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.springframework.util.StreamUtils.copyToString;

public class TrainerMock {

    public static void setupMockTrainerResponseResultSuccess(WireMockServer mockService) {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/v1/fit-coach/trainers/create"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value()) // Mock a successful response
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}"))); // Empty response body as your code doesn't use it
    }

    public static void setupMockTrainerResponseResultSpecializationNotFound(WireMockServer mockService) {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/v1/fit-coach/trainers/create"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value()) // Mock a NOT FOUND response
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Could not find specialization with id: "))); //
    }
}
