package com.krasnopolskyi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.security.dto.TraineeFullDto;
import com.krasnopolskyi.security.dto.TrainerFullDto;
import com.krasnopolskyi.security.dto.UserDto;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.user}")
    private String user;
    @Value("${spring.activemq.password}")
    private String password;
    @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);  // Convert the object to JSON as a text message
        converter.setTypeIdPropertyName("_typeId_");

        ObjectMapper objectMapper = new ObjectMapper();

        // Combine both mappings in a single map
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("trainer", TrainerFullDto.class);
        typeIdMappings.put("trainee", TraineeFullDto.class);
        typeIdMappings.put("user", UserDto.class);
        converter.setTypeIdMappings(typeIdMappings);

        // Register the module to handle Java 8 Date/Time (e.g., LocalDate)
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optionally disable timestamps

        converter.setObjectMapper(objectMapper); // Set the custom ObjectMapper



        return converter;
    }

    // Define the ActiveMQConnectionFactory bean
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(user);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter()); // Set the custom message converter
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-1"); // Control the number of concurrent listeners
        factory.setMessageConverter(jacksonJmsMessageConverter());
        return factory;
    }
}
