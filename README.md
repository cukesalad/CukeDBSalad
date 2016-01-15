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
  compile('jdbc driver for your db:1.0.0')
```
Create a DB connection details file - "dbsalad.properties" with below details:
```properties
db.url=jdbc:hsqldb:mem:testcase;shutdown=true
db.username=user
db.password=pwd
db.driver.class=org.hsqldb.jdbcDriver
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
  Given I set up data in DB using "insertusers.sql"
  And I teardown data in DB using "teardownusers.sql"
  Given I set up data in DB using "insertuserswithparams.sql" and below parameters:
  | key   | value |
  | id1   | 1     |
  | id2   | 2     |
  | id3   | 3     |
  And I teardown data in DB using "teardownuserswithparam.sql" and below parameters:
  | key   | value |
  | id1   | 1     |
  | id2   | 2     |
  | id3   | 3     |
  Given I set up data in DB using "insertusers.sql", and rollback test data at the end using "teardownusers.sql"
  Given I set up data in DB using "insertuserswithparams.sql", and rollback test data at the end using "teardownuserswithparam.sql" with below  parameters:
  | key   | value |
  | id1   | 1     |
  | id2   | 2     |
  | id3   | 3     |
  Given I set up data using the sql file "parameterisedinsertusers.sql", for the below data:
  | id | name      | email              |
  | 1  | Ned Stark | ned@gmail.com      |
  | 2  | Tyrion    | tyrion@yahoo.com   |
  | 3  | Daenerys  | daenerys@gmail.com |
  Then the result of the sql "selectuser.sql", is:
  | id | name      | email              |
  | 1  | Ned Stark | ned@gmail.com      |
  | 2  | Tyrion    | tyrion@yahoo.com   |
  | 3  | Daenerys  | daenerys@gmail.com |

```
## What if i have different DB instances/schemas for different environments?
Add files like ```dbsalad.dev.properties``` and pass a run time jvm arg like ```-Denv=dev```. The DB details of ```dbsalad.dev.properties``` will override ```dbsalad.properties``` which is the default

##Latest release:

Release 1.0.0

##How to contribute

These are just a few steps I could think of. If there are any other feature that you wish for, please go ahead and create the same in the [issue tracker](https://github.com/cukesalad/CukeDBSalad/issues). I will make best efforts to add them ASAP. If you wish contribute by coding, please fork the repository and raise a pull request.
