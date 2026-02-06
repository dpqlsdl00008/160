package database;

import server.ServerProperties;

public class MYSQLOption {

    public static final boolean isAdmin = true;
    public static final String MySQLURL = ServerProperties.getProperty("database.url");
    // public static final String MySQLURL = "jdbc:mysql://localhost:3306/maplestory?characterEncoding=euckr";
    public static final String MySQLUSER = ServerProperties.getProperty("database.user");
    public static final String MySQLPASS = ServerProperties.getProperty("database.password");

    public static int MySQLMINCONNECTION = 10;
    public static int MySQLMAXCONNECTION = 500000;
 /*   public static int innodb_lock_wait_timeout = 100;
    public static int open_files_limit = 32000;
    public static int max_connections = 20000;
    public static int wait_timeout = 60;*/
}
