Feature: User login and create training session

  Scenario: User login in system
    Given the user has valid credentials
    Given valid training session
    When the user logs into the system
    Then the user should receive a valid JWT token
    When the user create training session
    Then the user received success response about training
