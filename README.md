# CukeDBSalad
##Who can use this?

This module is intended to help in testing any application that has DB(RDBMS/JDBC) interactions. It gives you an intuitive way of applying changed to DB and validating data in DB. 

##Pre Requisites

JDK8/JDK7

##Getting Started

Lets say you want to test an application with DB interaction as a black box.

Create the project you want to use for testing. This will host all you feature files.
Add the below dependancy similar to - SampleCukeDBTest :
```gradle
  compile('org.cukesalad:CukeDBSalad:1.0.0')
```
Create feature files inside your project under src/main/resources/feature
Run the below commands for linux/mac:
```shell
> cd <git project root>
> sh gradlew clean build
> unzip build/distributions/<your project name>-1.0.0.zip -d build/distributions/
> sh build/distributions/<your project name>-1.0.0/bin/<your project name> org.cukesalad.db.runner.Runner
```
Sample feature file:
```gherkin
Feature: A feature to demonstrate DB cucumber util to setup/teardown/validate data in RDBMS

  Scenario: testing setup/teardown/validate data in RDBMS
    Given I start building a request with "POST" method and URL "https://graph.facebook.com/v2.5/me"
    And I add "access_token" equal to "invalid_access_token" as parameter to request
    And I add post body to the request as:
      """
        {
            "attr" : "any value"
        }
      """
    And I add the below values as headers to the request:
    | headerName    | headerValue       |
    | Content-Type  | application/json  |
    And I retrieve the resource
    Then the status code returned should be 400
    And The response should contain following headers:
      | headerName   | headerValue      |
      | Content-Type | application/json; charset=UTF-8 |
    And The response should contain "$..error.message" with value "Invalid OAuth access token."
    And The response should contain "$..error.type" with value "OAuthException"
    And The response should contain "$..error.code" with value "190"
    And The response should contain "$..error.fbtrace_id"
```

##Latest release:

Release 1.0.0

##How to contribute

These are just a few steps I could think of. If there are any other feature that you wish for, please go ahead and create the same in the [issue tracker](https://github.com/cukesalad/CukeDBSalad/issues). I will make best efforts to add them ASAP. If you wish contribute by coding, please fork the repository and raise a pull request.
