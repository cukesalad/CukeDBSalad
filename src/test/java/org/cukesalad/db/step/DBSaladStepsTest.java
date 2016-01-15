package org.cukesalad.db.step;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cukesalad.db.support.DBSaladContext;
import org.cukesalad.db.support.DBSaladHook;
import org.cukesalad.db.support.DynamicSQLQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cucumber.api.DataTable;

public class DBSaladStepsTest {

  private DBSaladSteps dbSaladSteps;
  private DBSaladHook dbSaladHook;
  @Before
  public void setUp() throws Exception {
    dbSaladSteps = new DBSaladSteps();
    dbSaladHook = new DBSaladHook();
    dbSaladHook.beforeHook();
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("createusertable.sql");
    DBSaladHook.executeUpdate(sqlQuery);
  }

  @After
  public void tearDown() throws Exception {
    dbSaladHook.afterHook();
  }

  @Test
  public void testI_setup_up_data_in_db_using_and_rollback_test_data_at_the_end_using() throws Throwable {

    dbSaladSteps.i_setup_up_data_in_db_using_and_rollback_test_data_at_the_end_using("insertusers.sql", "teardownusers.sql");
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    resultSet.next();
    assertEquals("1", resultSet.getString("id"));
    assertEquals("Ned Stark", resultSet.getString("name"));
    assertEquals("ned@gmail.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("2", resultSet.getString("id"));
    assertEquals("Tyrion", resultSet.getString("name"));
    assertEquals("tyrion@yahoo.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("3", resultSet.getString("id"));
    assertEquals("Daenerys", resultSet.getString("name"));
    assertEquals("daenerys@gmail.com", resultSet.getString("email"));
    
    assertTrue(DBSaladContext.tearDownFiles.get(0).getSqlFileName().equals("teardownusers.sql"));
  
  }

  @Test
  public void testCollectTearDownFiles() {
    Map<String, String> paramenterMap = new HashMap<String, String>();
    dbSaladSteps.collectTearDownFiles("teardownusers.sql", paramenterMap);
    assertEquals(DBSaladContext.tearDownFiles.get(0).getSqlFileName(), "teardownusers.sql");
    assertEquals(DBSaladContext.tearDownFiles.get(0).getParameterMap(), paramenterMap);
  }

  @Test
  public void testI_set_up_data_in_db_using() throws Throwable {
    dbSaladSteps.i_set_up_data_in_db_using("insertusers.sql");
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    resultSet.next();
    assertEquals("1", resultSet.getString("id"));
    assertEquals("Ned Stark", resultSet.getString("name"));
    assertEquals("ned@gmail.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("2", resultSet.getString("id"));
    assertEquals("Tyrion", resultSet.getString("name"));
    assertEquals("tyrion@yahoo.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("3", resultSet.getString("id"));
    assertEquals("Daenerys", resultSet.getString("name"));
    assertEquals("daenerys@gmail.com", resultSet.getString("email"));
  
  }

  @Test
  public void testI_teardown_data_in_db_using() throws Throwable {
    dbSaladSteps.i_set_up_data_in_db_using("insertusers.sql");
    dbSaladSteps.i_teardown_data_in_db_using("teardownusers.sql");
    
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    assertTrue("Expecting no result, but some result returned",!resultSet.next());
  
  }

  @Test
  public void testI_teardown_data_in_DB_using_and_below_parameters() throws Throwable {
    dbSaladSteps.i_set_up_data_in_db_using("insertusers.sql");
    List<List<String>> raw = new ArrayList<List<String>>();
    raw.add(Arrays.asList("key","value"));
    raw.add(Arrays.asList("id1","1"));
    raw.add(Arrays.asList("id2","2"));
    raw.add(Arrays.asList("id3","3"));
    DataTable parameters = DataTable.create(raw );
        
    dbSaladSteps.i_teardown_data_in_DB_using_and_below_parameters("teardownuserswithparam.sql", parameters);
    
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    assertTrue("Expecting no result, but some result returned",!resultSet.next());
  
  }

  @Test
  public void testI_set_up_data_in_DB_using_and_below_parameters() throws Throwable {
    List<List<String>> raw = new ArrayList<List<String>>();
    raw.add(Arrays.asList("key","value"));
    raw.add(Arrays.asList("id1","1"));
    raw.add(Arrays.asList("id2","2"));
    raw.add(Arrays.asList("id3","3"));
    DataTable parameters = DataTable.create(raw );

    dbSaladSteps.i_set_up_data_in_DB_using_and_below_parameters("insertuserswithparams.sql",parameters);
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    resultSet.next();
    assertEquals("1", resultSet.getString("id"));
    assertEquals("Ned Stark", resultSet.getString("name"));
    assertEquals("ned@gmail.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("2", resultSet.getString("id"));
    assertEquals("Tyrion", resultSet.getString("name"));
    assertEquals("tyrion@yahoo.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("3", resultSet.getString("id"));
    assertEquals("Daenerys", resultSet.getString("name"));
    assertEquals("daenerys@gmail.com", resultSet.getString("email"));
  
  }

  @Test
  public void testI_setup_up_data_in_DB_using_and_rollback_test_data_at_the_end_using_with_below_parameters() throws Throwable {
    List<List<String>> raw = new ArrayList<List<String>>();
    raw.add(Arrays.asList("key","value"));
    raw.add(Arrays.asList("id1","1"));
    raw.add(Arrays.asList("id2","2"));
    raw.add(Arrays.asList("id3","3"));
    DataTable parameters = DataTable.create(raw );

    dbSaladSteps.i_setup_up_data_in_DB_using_and_rollback_test_data_at_the_end_using_with_below_parameters("insertuserswithparams.sql", "teardownuserswithparam.sql",parameters);
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    resultSet.next();
    assertEquals("1", resultSet.getString("id"));
    assertEquals("Ned Stark", resultSet.getString("name"));
    assertEquals("ned@gmail.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("2", resultSet.getString("id"));
    assertEquals("Tyrion", resultSet.getString("name"));
    assertEquals("tyrion@yahoo.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("3", resultSet.getString("id"));
    assertEquals("Daenerys", resultSet.getString("name"));
    assertEquals("daenerys@gmail.com", resultSet.getString("email"));

    assertTrue(DBSaladContext.tearDownFiles.get(0).getSqlFileName().equals("teardownuserswithparam.sql"));
    assertTrue(DBSaladContext.tearDownFiles.get(0).getParameterMap().equals(dbSaladSteps.createParamMap(parameters)));

  
  }

  @Test
  public void testI_run_the_sql_file_for_the_below_data() throws Throwable{
    List<List<String>> raw = new ArrayList<List<String>>();
    raw.add(Arrays.asList("id","name", "email"));
    raw.add(Arrays.asList("1","Ned Stark", "ned@gmail.com"));
    raw.add(Arrays.asList("2","Tyrion", "tyrion@yahoo.com"));
    raw.add(Arrays.asList("3","Daenerys", "daenerys@gmail.com"));
    DataTable parameters = DataTable.create(raw );
    dbSaladSteps.i_run_the_sql_file_for_the_below_data("parameterisedinsertusers.sql", parameters);
    
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    resultSet.next();
    assertEquals("1", resultSet.getString("id"));
    assertEquals("Ned Stark", resultSet.getString("name"));
    assertEquals("ned@gmail.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("2", resultSet.getString("id"));
    assertEquals("Tyrion", resultSet.getString("name"));
    assertEquals("tyrion@yahoo.com", resultSet.getString("email"));
    
    resultSet.next();
    assertEquals("3", resultSet.getString("id"));
    assertEquals("Daenerys", resultSet.getString("name"));
    assertEquals("daenerys@gmail.com", resultSet.getString("email"));
  }
  
  @Test
  public void testThe_result_of_the_sql_is() throws Throwable{
    List<List<String>> raw = new ArrayList<List<String>>();
    raw.add(Arrays.asList("id","name", "email"));
    raw.add(Arrays.asList("1","Ned Stark", "ned@gmail.com"));
    raw.add(Arrays.asList("2","Tyrion", "tyrion@yahoo.com"));
    raw.add(Arrays.asList("3","Daenerys", "daenerys@gmail.com"));
    DataTable parameters = DataTable.create(raw );
    dbSaladSteps.i_run_the_sql_file_for_the_below_data("parameterisedinsertusers.sql", parameters);
    dbSaladSteps.the_result_of_the_sql_is("selectuser.sql", parameters);
  }
  
  @Test
  public void testCreateParamMap() {
    List<List<String>> raw = new ArrayList<List<String>>();
    raw.add(Arrays.asList("key","value"));
    raw.add(Arrays.asList("id1","1"));
    raw.add(Arrays.asList("id2","2"));
    raw.add(Arrays.asList("id3","3"));
    DataTable parameters = DataTable.create(raw );
    assertEquals(dbSaladSteps.createParamMap(parameters).size(), 3);
    assertTrue(!dbSaladSteps.createParamMap(parameters).containsKey("key"));
  }

}
