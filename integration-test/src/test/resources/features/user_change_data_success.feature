Feature: User login and change firstname and lastname

  Scenario: User login in system and change firstname and lastname
    Given the user has valid credentials
    When the user logs into the system
    Then the user should receive a valid JWT token
    When the trainer change firstname and lastname
    Then the firstname and lastname were changed
