package com.krasnopolskyi.report.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.report.entity.TrainingSession;
import com.krasnopolskyi.report.model.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
@Slf4j
public class JmsConfig {
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.user}")
    private String user;
    @Value("${spring.activemq.password}")
    private String password;

    @Value("${spring.activemq.listener.consumer.retry.delay}")
    private int retryDelay;

    @Value("${spring.activemq.listener.consumer.retry.max-attempts}")
    private int maxAttempts;

    @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_typeId_");

        ObjectMapper objectMapper = new ObjectMapper();

        // Mapping of the _typeId_ property to DTO classes
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("training.session", TrainingSession.class);
        typeIdMappings.put("report.trainer.data.updated", Trainer.class);
        converter.setTypeIdMappings(typeIdMappings);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optionally disable timestamps

        converter.setObjectMapper(objectMapper); // Set the custom ObjectMapper

        return converter;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(user);
        connectionFactory.setPassword(password);

        // Configure the RedeliveryPolicy for Dead Letter Queue handling
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(maxAttempts); // Corrected to use maxAttempts
        redeliveryPolicy.setInitialRedeliveryDelay(retryDelay); // Delay before first retry
        redeliveryPolicy.setBackOffMultiplier(2); // Exponential backoff

        redeliveryPolicy.setUseExponentialBackOff(true); // Enable exponential backoff

        // Set the DLQ for failed messages
        redeliveryPolicy.setDestination(new ActiveMQQueue("training.session.DLQ"));

        connectionFactory.setRedeliveryPolicy(redeliveryPolicy); // Apply the redelivery policy to the connection factory
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-5"); // Control the number of concurrent listeners
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setTransactionManager(jmsTransactionManager());

        // Error handler for logging the error
        factory.setErrorHandler(t -> {
            log.error("Error in listener, message failed: ", t);
        });

        return factory;
    }

    @Bean
    public PlatformTransactionManager jmsTransactionManager() {
        return new JmsTransactionManager(activeMQConnectionFactory());
    }
}
