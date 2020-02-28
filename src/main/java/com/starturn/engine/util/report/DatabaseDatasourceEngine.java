/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.report;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class DatabaseDatasourceEngine {
    private static final Logger logger = LogManager.getLogger(DatabaseDatasourceEngine.class);

    private final Properties prop;
    private ConnectionHelper connection;

    /**
     * Starts up the database engine using in-memory jdbc configuration. NOTE:
     * this engine only works with MICROSOFT SQL SERVER
     *
     * @throws Exception if SQL error occurs or microsoft driver class not found
     */
    public DatabaseDatasourceEngine() throws Exception {
        this.prop = JdbcProperties.getInstance();
    }

    /**
     * Starts up the database engine using customised jdbc configuration file.
     * NOTE: this engine only works with MICROSOFT SQL SERVER
     *
     * @param propFile path and name of the jdbc configuration file to load
     * @throws Exception if SQL error occurs or microsoft driver class not found
     */
    public DatabaseDatasourceEngine(String propFile) throws Exception {
        this.prop = JdbcProperties.getInstance(propFile);
    }

    /**
     * Starts up the database engine using customised jdbc configuration
     * properties object. NOTE: this engine only works with MICROSOFT SQL SERVER
     *
     * @param prop the properties object to use
     * @throws Exception if SQL error occurs or microsoft driver class not found
     */
    public DatabaseDatasourceEngine(Properties prop) throws Exception {
        this.prop = prop;
    }

    /**
     * Opens a connection to the database.
     *
     * @param type the type of connection to open (Oracle, Sql Server, My Sql, a
     * datasource, etc)
     * @throws SQLException if the connection isn't successful
     * @throws NamingException if the JNDI is invalid
     * @throws Exception if any other general issues occur
     */
    public void openConnection(ConnectionType type) throws SQLException, NamingException, Exception {
        connection = new ConnectionHelper(prop);
        connection.getPrepareConnection(type);
    }

    /**
     * Runs the provided sql script.
     *
     * @param sqlStatement the sql statement script
     * @param parameters the parameters to feed into the scripts
     * @return the result list, containing two items: (1) the headers, (2) the
     * data
     * @throws SQLException
     * @throws Exception
     */
    public List runStatement(String sqlStatement, Object... parameters) throws SQLException, Exception {
        if (parameters.length != countParameterBlocksInStatement(sqlStatement)) {
            logger.info("The parameter blocks in the statement does not match the number of parameters passed");
            throw new Exception("The parameter blocks in the statement does not match the number of parameters passed");
        }

        PreparedStatement statement = connection.getConnection().prepareStatement(sqlStatement);
        statement = insertParameters(statement, parameters);
        ResultSet rs = statement.executeQuery();
        List resultList = extractDataFromResultset(rs);
        statement.close();

        return resultList;
    }

    /**
     * A standard sql escape syntax to runs stored procedure without parameter
     * [this is peculiar for script that includes DML, DDL or/and DCL]
     *
     * @param sqlStatement the sql statement script
     * @param parameters the parameters to feed into the scripts
     * @return the result list, containing two items: (1) the headers, (2) the
     * data
     * @throws SQLException
     * @throws Exception
     */
    public List runNonParameterizedProcedureStatement(String sqlStatement, Object... parameters) throws SQLException, Exception {
        if (parameters.length != countParameterBlocksInStatement(sqlStatement)) {
            logger.info("The parameter blocks in the statement does not match the number of parameters passed");
            throw new Exception("The parameter blocks in the statement does not match the number of parameters passed");
        }

        logger.info("the sql escape syntax eventually sent to server is", sqlStatement);
        CallableStatement statement = connection.getConnection().prepareCall(sqlStatement);
        statement = insertProcedureParameters(statement, parameters);
        ResultSet rs = null;
        if (statement.execute()) {
            rs = statement.getResultSet();
        } else {
            logger.info("No record is found is the resultset object...");
        }

        List resultList = extractDataFromResultset(rs);
        return resultList;
    }

    /**
     * A standard sql escape syntax to runs stored procedure without parameter
     * [this is peculiar for script that includes DML, DDL or/and DCL]
     *
     * @param sqlStatement the sql statement script
     * @param parameters the parameters to feed into the scripts
     * @return the result list, containing two items: (1) the headers, (2) the
     * data
     * @throws SQLException
     * @throws Exception
     */
    public ResultSet runNonParameterizedProcedureStatement_ResultSet(String sqlStatement, Object... parameters) throws SQLException, Exception {
        if (parameters.length != countParameterBlocksInStatement(sqlStatement)) {
            logger.info("The parameter blocks in the statement does not match the number of parameters passed");
            throw new Exception("The parameter blocks in the statement does not match the number of parameters passed");
        }

        logger.info("the sql escape syntax eventually sent to server is", sqlStatement);
        CallableStatement statement = connection.getConnection().prepareCall(sqlStatement);
        statement = insertProcedureParameters(statement, parameters);
        ResultSet rs = null;
        if (statement.execute()) {
            rs = statement.getResultSet();
        } else {
            logger.info("No record is found is the resultset object...");
        }
        return rs;
    }

    /**
     * Commits the transaction. Must be called after
     * {@link #runStatement(java.lang.String, java.lang.Object...)}
     *
     * @throws SQLException
     */
    public void commitTransaction() throws SQLException {
        connection.getConnection().commit();
    }

    /**
     * Closes the connection to the database. Must be called after
     * {@link #commitTransaction()}
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        connection.getConnection().close();
    }

    /**
     * Gets the database connection. NOTE: the connection could very well be
     * invalid at the time of call.
     *
     * @return
     */
    public Connection getDatabaseConnection() {
        return connection.getConnection();
    }

    /**
     * Runs the provided sql script.
     *
     * @param sqlStatement the sql statement script
     * @param parameters the parameters to feed into the scripts
     * @return the result list, containing two items: (1) the headers, (2) the
     * data
     * @throws SQLException
     * @throws Exception
     */
    public ResultSet returnResultSet(String sqlStatement, Object... parameters) throws SQLException, Exception {
        if (parameters.length != countParameterBlocksInStatement(sqlStatement)) {
            logger.info("The parameter blocks in the statement does not match the number of parameters passed");
            throw new Exception("The parameter blocks in the statement does not match the number of parameters passed");
        }

        PreparedStatement statement = connection.getConnection().prepareStatement(sqlStatement);
        statement = insertParameters(statement, parameters);
        ResultSet rs = statement.executeQuery();

        return rs;
    }

    /**
     * Dynamically inserts query parameter into prepared statement script.
     *
     * @param statement the prepared statement
     * @param parameters the parameters to input into the prepared statement
     * @return the prepared statement with parameters in them
     * @throws SQLException
     */
    private PreparedStatement insertParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object parameterObj = parameters[i];
            if (parameterObj instanceof String) {
                statement.setString(i + 1, (String) parameterObj);
            } else if (parameterObj instanceof BigDecimal) {
                statement.setBigDecimal(i + 1, (BigDecimal) parameterObj);
            } else if (parameterObj instanceof Short) {
                statement.setShort(i + 1, ((Short) parameterObj));
            } else if (parameterObj instanceof Integer) {
                statement.setInt(i + 1, (Integer) parameterObj);
            } else if (parameterObj instanceof Long) {
                statement.setLong(i + 1, (Long) parameterObj);
            } else if (parameterObj instanceof Float) {
                statement.setFloat(i + 1, (Float) parameterObj);
            } else if (parameterObj instanceof Double) {
                statement.setDouble(i + 1, (Double) parameterObj);
            } else if (parameterObj instanceof java.util.Date) {
                java.util.Date uDate = (java.util.Date) parameterObj;
                java.sql.Date sDate = new java.sql.Date(uDate.getTime());
                statement.setDate(i + 1, sDate);
            } else if (parameterObj instanceof Boolean) {
                statement.setBoolean(i + 1, (Boolean) parameterObj);
            } else {
                statement.setObject(i + 1, parameterObj);//at index 0 for paramters, the first parameter is set to 1, hence i+1
            }

        }
        return statement;
    }

    /**
     * Dynamically inserts query parameter into prepared statement script.
     *
     * @param statement the prepared statement
     * @param parameters the parameters to input into the prepared statement
     * @return the prepared statement with parameters in them
     * @throws SQLException
     */
    private CallableStatement insertProcedureParameters(CallableStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);//at index 0 for paramters, the first parameter is set to 1, hence i+1
        }
        return statement;
    }

    /**
     * Counts the number of parameter entries required by the sql script.
     * Parameter blocks are denoted with a question mark (?)
     *
     * @param sqlStatement the sql script
     * @return the number of parameter entries
     */
    private int countParameterBlocksInStatement(String sqlStatement) {
        char where = '?';
        int count = 0;
        for (int i = 0; i < sqlStatement.length(); i++) {
            if (sqlStatement.charAt(i) == where) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Extracts the headers and data content from the prepared statement's
     * result set.
     *
     * @param resultset the result set
     * @return the list containing the headers and data content
     * @throws Exception
     */
    private List extractDataFromResultset(ResultSet resultset) throws Exception {
        List<String> columnheaders = new ArrayList<>();
        List<Object[]> datacontent = new ArrayList<>();
        List records = new ArrayList();
        if (resultset != null) {
            int noOfColumns = resultset.getMetaData().getColumnCount();
            //get columns
            columnheaders.add("S/N");
            for (int i = 0; i < noOfColumns; i++) {
                columnheaders.add(resultset.getMetaData().getColumnLabel(i + 1));//value in result set starts from 1
            }

            //get data contents
            while (resultset.next()) {
                if (noOfColumns <= 0) {
                    logger.info("Result set is empty");
                }
                Object[] resultsetArray = new Object[noOfColumns];
                for (int i = 0; i < noOfColumns; i++) {
                    //gather each column content into a row
                    //value in result set starts from 1, hence the i + 1
                    resultsetArray[i] = resultset.getObject(i + 1);
                }
                //insert row into data content list
                datacontent.add(resultsetArray);
            }
            records.add(columnheaders);
            records.add(datacontent);
            resultset.close();
        }

        return records;
    }
}
