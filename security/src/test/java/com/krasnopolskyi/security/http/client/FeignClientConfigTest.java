package com.krasnopolskyi.security.http.client;

import com.krasnopolskyi.security.service.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    void requestInterceptor_ShouldAddAuthorizationHeader() {
        // Arrange
        String expectedToken = "mockedToken";
        when(jwtService.generateServiceToken()).thenReturn(expectedToken);

        RequestInterceptor interceptor = feignClientConfig.requestInterceptor();

        // Act
        interceptor.apply(requestTemplate);

        // Assert
        verify(jwtService, times(1)).generateServiceToken(); // Verify token generation is called
        assert requestTemplate.headers().containsKey("Authorization"); // Check if the header is added
        assert requestTemplate.headers().get("Authorization").contains("Bearer " + expectedToken); // Check if the correct token is added
    }
}
