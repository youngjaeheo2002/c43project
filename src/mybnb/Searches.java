package mybnb;

import java.sql.*;

public class Searches {
    Connection connection;

    public Searches() {
        try {
            this.connection = SQLdriver.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Write search related queries here
}
