package mybnb;

import java.sql.*;

public class SQLdriver {
    private static Connection connection;

    private static final String uriDB = "jdbc:mysql://localhost:3306/mybnb";
    private static final String username = "root";
    private static final String password = "qwerty";

    private SQLdriver()  {}

    private static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(uriDB, username, password);
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            connection = createConnection();
        }
        return connection;
    }
}
