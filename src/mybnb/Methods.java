package mybnb;

import java.sql.*;

public abstract class Methods {
    Connection connection;

    public Methods() {
        try {
            this.connection = SQLdriver.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
