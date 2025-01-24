Feature: User Authentication

  Scenario: User logs in and creates a new training
    Given the user has valid credentials
    When the user logs into the system
    Then the user should receive a valid JWT token
