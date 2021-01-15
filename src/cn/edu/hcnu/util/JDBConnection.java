package cn.edu.hcnu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 public  class  JDBConnection {
    public static Connection getConnection() throws SQLException {
        String  url ="jdbc:oracle:thin:@localhost:1521:orcl";
        String username = "opts";
        String password = "opts1234";
        Connection  con = DriverManager.getConnection(url,username,password);
        return con;
    }
}
