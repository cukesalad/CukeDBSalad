package org.cukesalad.db.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features={"classpath:feature"}, glue={"classpath:com.cukesalad"}, plugin={"pretty", "html:target/cukesalad", "json:target/cukesalad.json", "junit:target/cukesalad.xml"})
public class Runner {

}
