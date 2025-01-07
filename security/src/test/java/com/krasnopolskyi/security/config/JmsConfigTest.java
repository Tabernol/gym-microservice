package com.krasnopolskyi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableJms
class JmsConfigTest {

    @InjectMocks
    private JmsConfig jmsConfig;

    @Mock
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @BeforeEach
    void setUp() {
        // Set the values for @Value properties using ReflectionTestUtils
        ReflectionTestUtils.setField(jmsConfig, "brokerUrl", "tcp://localhost:61616");
        ReflectionTestUtils.setField(jmsConfig, "user", "admin");
        ReflectionTestUtils.setField(jmsConfig, "password", "admin");
    }

    @Test
    void testJacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = jmsConfig.jacksonJmsMessageConverter();

        assertNotNull(converter);
    }

    @Test
    void testActiveMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = jmsConfig.activeMQConnectionFactory();

        assertNotNull(connectionFactory);
        assertEquals("tcp://localhost:61616", connectionFactory.getBrokerURL());
        assertEquals("admin", connectionFactory.getUserName());
        assertEquals("admin", connectionFactory.getPassword());
    }

    @Test
    void testJmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = jmsConfig.jmsListenerContainerFactory(activeMQConnectionFactory);

        assertNotNull(factory);
        assertEquals("1-1", ReflectionTestUtils.getField(factory, "concurrency"));
    }
}
