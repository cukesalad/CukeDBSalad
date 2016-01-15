package org.cukesalad.db.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class DBSaladHookTest {

  @Before
  public void setUp() {
    DBSaladHook.refresh();
  }
  
  @Test
  public void testRefresh() throws IOException, SQLException {
    DBSaladHook.refresh();
    assertNull(DBSaladContext.batchquery);
    assertTrue(DBSaladContext.tearDownFiles.isEmpty());
    assertTrue(DBSaladContext.dbprops.isEmpty());
  }

  @Test
  public void testExecuteSQL() throws IOException, SQLException {
    DBSaladContext.dbenv = null;
    DBSaladHook dbHook = new DBSaladHook();
    assertNull(DBSaladContext.dbconnection);
    dbHook.beforeHook();
    assertNotNull(DBSaladContext.dbconnection);
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName("createusertable.sql");
    DBSaladHook.executeUpdate(dynamicSQLQuery);
    dynamicSQLQuery.setSqlFileName("insertusers.sql");
    DBSaladHook.executeUpdate(dynamicSQLQuery);
    dbHook.afterHook();
  }

  @Test
  public void testTearDownDBTestData() throws IOException, SQLException {
    DBSaladContext.dbenv = null;
    DBSaladHook dbHook = new DBSaladHook();
    assertNull(DBSaladContext.dbconnection);
    dbHook.beforeHook();
    assertNotNull(DBSaladContext.dbconnection);
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName("createusertable.sql");
    DBSaladHook.executeUpdate(dynamicSQLQuery);
    dynamicSQLQuery.setSqlFileName("insertusers.sql");
    DBSaladHook.executeUpdate(dynamicSQLQuery);
    
    DynamicSQLQuery tearDownSQLQuery = new DynamicSQLQuery();
    tearDownSQLQuery.setSqlFileName("teardownusers.sql");
    DBSaladContext.tearDownFiles.add(tearDownSQLQuery );
    DBSaladHook.tearDownDBTestData();
    
    DynamicSQLQuery sqlQuery = new DynamicSQLQuery();
    sqlQuery.setSqlFileName("selectuser.sql");
    ResultSet resultSet = DBSaladHook.executeQuery(sqlQuery );
    assertTrue("Expecting no result, but some result returned",!resultSet.next());
    
    dbHook.afterHook();
  }
  
  @Test
  public void testExecuteQuery() throws IOException, SQLException {
    DBSaladContext.dbenv = null;
    DBSaladHook dbHook = new DBSaladHook();
    assertNull(DBSaladContext.dbconnection);
    dbHook.beforeHook();
    assertNotNull(DBSaladContext.dbconnection);
    DynamicSQLQuery dynamicSQLQuery = new DynamicSQLQuery();
    dynamicSQLQuery.setSqlFileName("createusertable.sql");
    DBSaladHook.executeUpdate(dynamicSQLQuery);
    dynamicSQLQuery.setSqlFileName("insertusers.sql");
    DBSaladHook.executeUpdate(dynamicSQLQuery);
        
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
    
    dbHook.afterHook();
  }

  @Test
  public void testCloseDBConnection() throws IOException, SQLException {
    DBSaladContext.dbenv = null;
    DBSaladHook dbHook = new DBSaladHook();
    assertNull(DBSaladContext.dbconnection);
    dbHook.beforeHook();
    assertNotNull(DBSaladContext.dbconnection);

    DBSaladHook.closeDBConnection();
    assertTrue(DBSaladContext.dbconnection.isClosed());
  } 
  @Test
  public void testLoadProperties() {
    DBSaladHook.loadProperties();
    assertEquals("pwd", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_PWD));
    assertEquals("jdbc:hsqldb:mem:testcase;shutdown=true", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_URL));
    assertEquals("user", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_USERNAME));

    DBSaladContext.dbenv = "test";
    DBSaladHook.loadProperties();
    assertEquals("testpwd", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_PWD));
    assertEquals("testdburl", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_URL));
    assertEquals("testuser", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_USERNAME));

    DBSaladContext.dbenv = "dev";
    DBSaladHook.loadProperties();
    assertEquals("devpwd", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_PWD));
    assertEquals("devdburl", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_URL));
    assertEquals("devuser", DBSaladContext.dbprops.getProperty(DBSaladConstants.DB_USERNAME));

  }

}
