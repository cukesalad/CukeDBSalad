package org.cukesalad.db.support;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBSaladContext {
  public static String                batchquery;
  public static List<DynamicSQLQuery> tearDownFiles = new ArrayList<DynamicSQLQuery>();
  public static Connection            dbconnection;
  public static Properties            dbprops = new Properties();
  public static String                dbenv = System.getProperty(DBSaladConstants.ENV);;

}
