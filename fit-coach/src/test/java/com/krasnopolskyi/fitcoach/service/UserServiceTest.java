package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.User;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.GymException;
import com.krasnopolskyi.fitcoach.repository.UserRepository;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private JmsTemplate jmsTemplate;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setActive(true);
    }

    @Test
    void updateRemoteUser_shouldSendMessagesToReportAndSecurity() throws JMSException {
        // Arrange
        User user = new User(1L, "John", "Doe", "john.doe", true);

        // Act
        userService.updateRemoteUser(user);

        // Assert
        // Verify message sent to "report.trainer.data.updated"
        verify(jmsTemplate).convertAndSend(eq("report.trainer.data.updated"), eq(user), any());

        // Verify message sent to "security.user.data.updated"
        verify(jmsTemplate).convertAndSend(eq("security.user.data.updated"), eq(user), any());

        // Verify that the message properties are set for both messages
        verify(jmsTemplate, times(2)).convertAndSend(anyString(), eq(user), any());
    }
}
