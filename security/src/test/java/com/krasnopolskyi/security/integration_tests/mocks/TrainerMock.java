package com.krasnopolskyi.security.integration_tests.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class TrainerMock {

    public static void setupMockTrainerResponse(WireMockServer mockService) {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/v1/fit-coach/trainers/create"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value()) // Mock a successful response
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}"))); // Empty response body as your code doesn't use it
    }

//    public static void setupMockTrainerResponse(WireMockServer mockService) {
//        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/v1/fit-coach/auth/sign-up/trainer"))
//                .willReturn(WireMock.aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                                .withBody("")));
////                        .withBody(
////                                copyToString(TrainerMock.class.getClassLoader()
////                                                .getResourceAsStream("mocks/trainer.json"),
////                                        defaultCharset()))));
//    }
}
