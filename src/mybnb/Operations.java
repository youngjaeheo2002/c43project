package mybnb;

import java.sql.*;

public class Operations {
    Connection connection;

    public Operations() {
        try {
            this.connection = SQLdriver.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Write operation related queries here
}
