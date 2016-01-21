package org.cukesalad.db.step;

import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cukesalad.db.support.DBSaladConstants;
import org.cukesalad.db.support.DBSaladContext;
import org.cukesalad.db.support.DBSaladHook;
import org.cukesalad.db.support.DynamicSQLQuery;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DBSaladSteps {

  @When("^I set up data in DB using \"([^\"]*)\", and rollback test data at the end using \"([^\"]*)\"$")
  public void i_setup_up_data_in_db_using_and_rollback_test_data_at_the_end_using(String setupFile, String tearDownFile)
      throws Throwable {
    // first clean dirty data
    i_teardown_data_in_db_using(tearDownFile);
    // then setup data
    i_set_up_data_in_db_using(setupFile);
    collectTearDownFiles(tearDownFile, null);
  }

  public static void collectTearDownFiles(String tearDownFile, Map<String, String> paramenterMap) {
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setParameterMap(paramenterMap);
    dynamicSQLQuery.setSqlFileName(tearDownFile);
    DBSaladContext.tearDownFiles.add(dynamicSQLQuery);
  }

  @When("^I set up data in DB using \"([^\"]*)\"$")
  public void i_set_up_data_in_db_using(String setupFile) throws Throwable {
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName(setupFile);
    DBSaladHook.executeUpdate(dynamicSQLQuery);
  }

  @When("^I teardown data in DB using \"([^\"]*)\"$")
  public void i_teardown_data_in_db_using(String tearDownFile) throws Throwable {
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName(tearDownFile);
    DBSaladHook.executeUpdate(dynamicSQLQuery);
  }

  @Given("^I teardown data in DB using \"([^\"]*)\" and below parameters:$")
  public void i_teardown_data_in_DB_using_and_below_parameters(String sqlFileName, DataTable parameters) throws Throwable {
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName(sqlFileName);
    Map<String, String> parameterMap = createParamMap(parameters);
    dynamicSQLQuery.setParameterMap(parameterMap);
    DBSaladHook.executeUpdate(dynamicSQLQuery);
  }

  
  @Given("^I set up data in DB using \"([^\"]*)\" and below parameters:$")
  public void i_set_up_data_in_DB_using_and_below_parameters(String sqlFileName, DataTable parameters) throws Throwable {
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName(sqlFileName);
    Map<String, String> parameterMap = createParamMap(parameters);
    dynamicSQLQuery.setParameterMap(parameterMap);
    DBSaladHook.executeUpdate(dynamicSQLQuery);
  }

  @Given("^I set up data in DB using \"([^\"]*)\", and rollback test data at the end using \"([^\"]*)\" with below parameters:$")
  public void i_setup_up_data_in_DB_using_and_rollback_test_data_at_the_end_using_with_below_parameters(String setupFileName, String tearDownFileName, DataTable parameters) throws Throwable {
 // first clean dirty data
    i_teardown_data_in_DB_using_and_below_parameters(tearDownFileName, parameters);
    // then setup data
    i_set_up_data_in_DB_using_and_below_parameters(setupFileName, parameters);
    collectTearDownFiles(tearDownFileName, createParamMap(parameters));
  }

  @Given("^I set up data using the sql file \"([^\"]*)\" for the below data:$")
  public void i_set_up_data_using_the_sql_file_for_the_below_data(String setupFileName, DataTable parameters) throws Throwable {
    List<Map<String, String>> paramMapList = parameters.asMaps(String.class, String.class);
    for (Map<String, String> paramMap : paramMapList) {
      DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
      dynamicSQLQuery.setSqlFileName(setupFileName);
      dynamicSQLQuery.setParameterMap(paramMap);
      DBSaladHook.executeUpdate(dynamicSQLQuery);
    }
  }
  
  @Then("^the result of the sql \"([^\"]*)\" is:$")
  public void the_result_of_the_sql_is(String setupFileName, DataTable expectedResults) throws Throwable {
    List<Map<String, String>> expectedResultsMaps = expectedResults.asMaps(String.class, String.class);
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName(setupFileName);
    ResultSet result = DBSaladHook.executeQuery(dynamicSQLQuery);
    List<String> columnNames = expectedResults.topCells();
    List<Map<String, String>> actualResultsMaps = new ArrayList<Map<String,String>>();
    while (result.next()) {
      Map<String, String> actualRow = new HashMap<String, String>();
      for (String columnName : columnNames) {
        actualRow.put(columnName, result.getString(columnName));
      }
      actualResultsMaps.add(actualRow);
    }
    assertTrue(actualResultsMaps.containsAll(expectedResultsMaps));
  }

  
  public Map<String, String> createParamMap(DataTable parameters) {
    Map<String, String> parameterMap = new HashMap<String, String>(parameters.asMap(String.class, String.class));
    parameterMap.remove(DBSaladConstants.PARAM_MAP_KEY);
    return parameterMap;
  }
}
