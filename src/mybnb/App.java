package mybnb;

import java.sql.SQLException;

public class App {
    public static void main() {
        try {
            MySqlDAO dao = new MySqlDAO();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
