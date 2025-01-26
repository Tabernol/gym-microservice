package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final JmsTemplate jmsTemplate;

    @Transactional(transactionManager = "jmsTransactionManager")
    public void updateRemoteUser(User user) {
        // send message to report microservice
        jmsTemplate.convertAndSend("report.trainer.data.updated", user, message -> {
            message.setStringProperty("_typeId_", "report.trainer.data.updated");
            return message;
        });

        // send message to security microservice
        jmsTemplate.convertAndSend("security.user.data.updated", user, message -> {
            message.setStringProperty("_typeId_", "security.user.data.updated");
            return message;
        });
    }
}
