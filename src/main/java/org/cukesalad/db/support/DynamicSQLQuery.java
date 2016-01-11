package org.cukesalad.db.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

public class DynamicSQLQuery {

  private String              sqlFileName;
  private Map<String, String> parameterMap;

  public String getSqlFileName() {
    return sqlFileName;
  }

  public void setSqlFileName(String sqlFileName) {
    this.sqlFileName = sqlFileName;
  }


  public Map<String, String> getParameterMap() {
    return parameterMap;
  }


  public void setParameterMap(Map<String, String> parameterMap) {
    this.parameterMap = parameterMap;
  }


  public String getSqlQuery() throws IOException {
    InputStream stream = getInputStreamFromFile(DBSaladConstants.SQL_DIR + sqlFileName);
    StringWriter writer = new StringWriter();
    IOUtils.copy(stream, writer);
    String rawSql = writer.toString();
    String batchQuery = manipulateBatchQuery(rawSql, parameterMap);
    return batchQuery;
  }
  public static InputStream getInputStreamFromFile(String fileName){
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream stream = loader.getResourceAsStream(fileName);
    return stream;
  }
  public List<String> getBatchQueryList() throws IOException {
    List<String> batchQueries = Arrays.asList(getSqlQuery().split(";"));
    return batchQueries;

  }

  private static String manipulateBatchQuery(String rawSql, Map<String, String> parameterMap) {
    String batchQuery = new String(rawSql);
    if (parameterMap != null) {
      for (Entry<String, String> entry : parameterMap.entrySet()) {
        batchQuery = batchQuery.replaceAll(DBSaladConstants.PARAM_PREFIX+entry.getKey(), entry.getValue());
      }
    }
    return batchQuery;
  }
}
