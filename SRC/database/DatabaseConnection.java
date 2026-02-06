/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package database;

import server.ServerProperties;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Connection;

import java.util.logging.Logger;
import java.util.logging.Level;
import javax.sql.DataSource;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
/**
 * All OdinMS servers maintain a Database Connection. This class therefore "singletonices" the connection per process.
 * 
 * 
 * @author Frz
 */
public class DatabaseConnection {
    private static DataSource dataSource;
    private static GenericObjectPool connectionPool;
    private static String databaseName;
    private static int databaseMajorVersion;
    private static int databaseMinorVersion;
    private static String databaseProductVersion;

    public synchronized static void init() {
        if (dataSource != null) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Throwable ex) {
            System.exit(1);
        }

        connectionPool = new GenericObjectPool();
        if (MYSQLOption.MySQLMINCONNECTION > MYSQLOption.MySQLMAXCONNECTION) {
            MYSQLOption.MySQLMAXCONNECTION = MYSQLOption.MySQLMINCONNECTION;
        }

        connectionPool.setMaxIdle(MYSQLOption.MySQLMINCONNECTION);
        connectionPool.setMaxActive(MYSQLOption.MySQLMAXCONNECTION);
        connectionPool.setTestOnBorrow(true);
        connectionPool.setMaxWait(5000);

        try {
            dataSource = setupDataSource();
            Connection c = getConnection();
            DatabaseMetaData dmd = c.getMetaData();
            databaseName = dmd.getDatabaseProductName();
            databaseMajorVersion = dmd.getDatabaseMajorVersion();
            databaseMinorVersion = dmd.getDatabaseMinorVersion();
            databaseProductVersion = dmd.getDatabaseProductVersion();
            c.close();
        } catch (Exception e) {
            System.err.println("[알림] DB 초기화에 실패하였습니다. DB서버가 올바르게 켜져있는지, DB 사용자 설정은 올바른지 확인해주세요.\r\n" + e.toString());
            System.exit(1000);
        }
        System.err.println("[알림] DB Version : " + databaseName + "   " + databaseProductVersion);
        System.err.println("[알림] MinConnection : " + connectionPool.getMaxIdle() + " MaxConnection : " + connectionPool.getMaxActive() + "\r\n");
    }

   /*private static DataSource setupDataSource() throws Exception {
        ConnectionFactory conFactory = new DriverManagerConnectionFactory(MYSQLOption.MySQLURL, MYSQLOption.MySQLUSER, MYSQLOption.MySQLPASS);
        PoolableConnectionFactoryAE poolableConnectionFactoryAE = new PoolableConnectionFactoryAE(conFactory, connectionPool, null, 1, false, true);
        return new PoolingDataSource(connectionPool);
    }*/
    
    private static DataSource setupDataSource() throws Exception {
        ConnectionFactory conFactory = new DriverManagerConnectionFactory(ServerProperties.getProperty("database.url"), ServerProperties.getProperty("database.user"), ServerProperties.getProperty("database.password"));

        new PoolableConnectionFactoryAE(conFactory, connectionPool, null, 1, false, true);

        return new PoolingDataSource(connectionPool);
    }
    /*
            MYSQLOption.MySQLURL = ServerProperties.getProperty("database.url");
        MYSQLOption.MySQLUSER = ServerProperties.getProperty("database.user");
        MYSQLOption.MySQLPASS = ServerProperties.getProperty("database.password");
    */
    
    public static void closeObject(Connection con) {
        try {
            con.close();
        } catch (Exception ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized void shutdown() {
        try {
            connectionPool.close();
        } catch (Exception e) {
        }

        dataSource = null;
    }

    public static Connection getConnection() throws SQLException {
        if (connectionPool.getNumIdle() == 0) {
            connectionPool.setMaxActive(Math.min(connectionPool.getMaxActive() + 1, 100000));
        }
        final Connection con = dataSource.getConnection();
        return con;
    }
    
    public static Connection getDumpConnection() throws SQLException {
        DatabaseConnection.init();
        if (connectionPool.getNumIdle() == 0) {
            connectionPool.setMaxActive(Math.min(connectionPool.getMaxActive() + 1, 100000));
        }
        final Connection con = dataSource.getConnection();
        return con;
    }

    public static int getActiveConnections() {
        return connectionPool.getNumActive();
    }

    public static int getIdleConnections() {
        return connectionPool.getNumIdle();
    }

    public static final int CLOSE_CURRENT_RESULT = 1;
    public static final int KEEP_CURRENT_RESULT = 2;
    public static final int CLOSE_ALL_RESULTS = 3;
    public static final int SUCCESS_NO_INFO = -2;
    public static final int EXECUTE_FAILED = -3;
    public static final int RETURN_GENERATED_KEYS = 1;
    public static final int NO_GENERATED_KEYS = 2;
}