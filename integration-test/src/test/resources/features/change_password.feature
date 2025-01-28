Feature: Change password

  Scenario: User login in system and change password then login again
    Given the user has valid credentials
    When the user logs into the system
    Then the user should receive a valid JWT token
    When the user sends request to change password
    When the user logs into the system
    Then the user should receive a valid JWT token
