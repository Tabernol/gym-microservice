Feature: User received report

  Scenario: User login in system
    Given the user has valid credentials
    Given valid training session
    When the user logs into the system
    Then the user should receive a valid JWT token
    When the user ask report
    Then the user received report
