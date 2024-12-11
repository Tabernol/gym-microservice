package com.krasnopolskyi.fitcoach.config;

import com.krasnopolskyi.fitcoach.http.interceptor.ControllerLogInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {
    @Mock
    private ControllerLogInterceptor controllerLogInterceptor;

    private WebConfig webConfig;

    @Test
    void webConfigTest() {
        webConfig = new WebConfig(controllerLogInterceptor);
        assertNotNull(webConfig);
    }

}
