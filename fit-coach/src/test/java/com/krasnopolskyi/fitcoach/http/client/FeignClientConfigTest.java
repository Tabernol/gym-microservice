package com.krasnopolskyi.fitcoach.http.client;

import com.krasnopolskyi.fitcoach.service.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FeignClientConfigTest {
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private FeignClientConfig feignClientConfig;

    private RequestTemplate requestTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestTemplate = new RequestTemplate();
    }

    @Test
    void testRequestInterceptorAddsAuthorizationHeader() {
        // Arrange
        String token = "mockedToken";
        when(jwtService.generateServiceToken()).thenReturn(token);

        // Act
        RequestInterceptor interceptor = feignClientConfig.requestInterceptor();
        interceptor.apply(requestTemplate);

        // Assert
        verify(jwtService, times(1)).generateServiceToken();
        assertEquals("Bearer " + token, requestTemplate.headers().get("Authorization").iterator().next());
    }
}
