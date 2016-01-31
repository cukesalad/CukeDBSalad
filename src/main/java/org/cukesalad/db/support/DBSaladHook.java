package org.cukesalad.db.support;

import static org.cukesalad.db.support.DBSaladContext.dbconnection;
import static org.cukesalad.db.support.DBSaladContext.dbenv;
import static org.cukesalad.db.support.DBSaladContext.dbprops;
import static org.cukesalad.db.support.DBSaladContext.tearDownFiles;
import static org.cukesalad.db.support.DBSaladContext.batchquery;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class DBSaladHook {
  static final Logger LOG = LoggerFactory.getLogger(DBSaladHook.class);
  
  public static void refresh() {
    tearDownFiles = new ArrayList<DynamicSQLQuery>();
    dbprops = new Properties();
    batchquery = null;
    dbconnection = null;
  }

  @Before()
  public void beforeHook() {
    refresh();
    loadProperties();
    setupDBConnection();
  }

  public static void setupDBConnection() {

    try {
      Class.forName(dbprops.getProperty(DBSaladConstants.DB_DRIVER_CLASS));
    } catch (ClassNotFoundException e) {
      LOG.error("Error loading db driver class", e);
    }
    LOG.info("DB Driver class Registered!");
    try {
      dbconnection = DriverManager.getConnection(dbprops.getProperty(DBSaladConstants.DB_URL),
          dbprops.getProperty(DBSaladConstants.DB_USERNAME), dbprops.getProperty(DBSaladConstants.DB_PWD));
    } catch (SQLException e) {
      LOG.error("DB Connection Failed! Check output console", e);
    }
  }

  @After
  public void afterHook() throws IOException, SQLException {
    tearDownDBTestData();
    closeDBConnection();
    refresh();
  }

  public static void tearDownDBTestData() throws IOException, SQLException {
    for (DynamicSQLQuery tearDownFile : tearDownFiles) {
      executeUpdate(tearDownFile);
    }
  }

  public static void executeUpdate(DynamicSQLQuery sqlQuery) throws IOException, SQLException {
    List<String> batchQueries = sqlQuery.getBatchQueryList();
    Statement stmt = null;
    try {
      stmt = dbconnection.createStatement();
      for (String eachQuery : batchQueries) {
        if (!eachQuery.trim().isEmpty()) {
          stmt.addBatch(eachQuery);
        }
      }
      int[] updateCounts = stmt.executeBatch();
      dbconnection.commit();
      System.out.println("the update counts for \n[" + sqlQuery.getSqlQuery() + "] are - \n"
          + new ObjectMapper().writeValueAsString(updateCounts));
    } catch (SQLException e) {
      System.out.println("Failed to execute file - " + sqlQuery.getSqlFileName() + "! Check output console");
      e.printStackTrace();
      throw e;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println("Failed to close statement while executing file - " + sqlQuery.getSqlFileName() + "! Check output console");
          e.printStackTrace();
          throw e;
        }
      }
    }
  }
  
  public static ResultSet executeQuery(DynamicSQLQuery sqlQuery) throws IOException, SQLException {
    List<String> batchQueries = sqlQuery.getBatchQueryList();
    Statement stmt = null;
    ResultSet resultSet = null;
    try {
      stmt = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      for (String eachQuery : batchQueries) {
        if (!eachQuery.trim().isEmpty()) {
          resultSet = stmt.executeQuery(eachQuery);
        }
      }
      
      //dbconnection.commit();
      System.out.println("result set of the query \n[" + sqlQuery.getSqlQuery() + "] are - \n"
          + resultSet);
    } catch (SQLException e) {
      System.out.println("Failed to execute file - " + sqlQuery.getSqlFileName() + "! Check output console");
      e.printStackTrace();
      throw e;
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          System.out.println("Failed to close statement while executing file - " + sqlQuery.getSqlFileName() + "! Check output console");
          e.printStackTrace();
          throw e;
        }
      }
    }
    return resultSet;
  }

  public static void closeDBConnection() {
    try {
      dbconnection.close();
    } catch (SQLException e) {
      System.out.println("Failed to close DB connection! Check output console");
      e.printStackTrace();
    }
  }

  public static void loadProperties() {
    try {

      LOG.debug("loading db salad Properties");
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      InputStream stream = loader.getResourceAsStream("dbsalad.properties");
      if (stream != null) {
        dbprops.load(stream);
      }
      if (dbenv != null) {
        stream = loader.getResourceAsStream("dbsalad.{env}.properties".replace("{env}", dbenv));
        if (stream != null) {
          dbprops.load(stream);
        }
      }
      dbprops.putAll(System.getProperties());
      LOG.debug("db salad Properties successfully loaded");
    } catch (IOException ex) {
      LOG.error("Error loading db salad Properties", ex);
    }
  }
}
