package db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {
    public static Connection getConnection(){

        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://LAPTOP-8IHQTB5B\\MSSQLSERVER1:1433;databaseName=HospitalDB;encrypt=true;trustServerCertificate=true;";
            String userName = "sa";
            String password = "123456";

            connection = DriverManager.getConnection(url,userName,password);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection){
        try {
            if(connection!=null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
