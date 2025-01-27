Feature: Create Trainer

  Scenario: User creates a new account and login in system
    Given the user has valid form for creating trainer
    When the user sends request to create trainer
    Then the user received credentials
    When the user logs into the system
    Then the user should receive a valid JWT token

