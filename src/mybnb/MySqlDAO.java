package mybnb;

import java.sql.*;

public class MySqlDAO {
    private Connection connection;

    private final String uriDB = "jdbc:mysql://localhost:3306/mybnb";
    private final String username = "root";
    private final String password = "qwerty";

    public MySqlDAO() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.connection = DriverManager.getConnection(this.uriDB, this.username, this.password);
    }

    // write SQL methods here
}
