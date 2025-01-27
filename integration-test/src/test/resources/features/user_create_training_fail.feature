Feature: User login and fail creating training session

  Scenario: User login in system and fail creating training session with does not exist user
    Given the user has valid credentials
    Given invalid training session user is not exist
    When the user logs into the system
    Then the user should receive a valid JWT token
    When the user create training session
    Then the user received fail response about training


  Scenario: User login in system nd fail creating training session invalid date
    Given the user has valid credentials
    Given invalid training session invalid date
    When the user logs into the system
    Then the user should receive a valid JWT token
    When the user create training session
    Then the user received fail response about date training
