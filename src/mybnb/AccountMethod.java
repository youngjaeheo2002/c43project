package mybnb;

import java.sql.*;
import java.util.ArrayList;

public class AccountMethod {
    Connection connection;

    public AccountMethod() {
        try {
            this.connection = SQLdriver.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Write operation related queries here

    public void SignUp(String username, String password,Date dob, String address,String occupation,int sin){
        try{
           
            PreparedStatement account = connection.prepareStatement("INSERT INTO accounts (username,password,dob,address,occupation,sin) VALUES ( '"+username+"','"+password+"','"+address+"', '"+occupation+"', '"+sin+"')");
            account.executeUpdate();
        }

        catch(Exception e){
            System.out.println(e);

        }
        finally{
            System.out.println("SignUp Completed");
        }
    }

    public boolean Login(String username,String password) throws Exception{
        try{
            PreparedStatement account = connection.prepareStatement("SELECT * FROM accounts WHERE username = '"+username+"' AND password = '"+password+"'");
        

            ResultSet result = account.executeQuery();
            ArrayList<String> array = new ArrayList<String>();
            while(result.next() ){
                array.add (result.getString("username"));
               
            }
            return !(array.isEmpty());
        }

        catch(Exception e){
            System.out.println(e);
        }

        finally{
            System.out.println("Login complete");
        }
        return false;
    }
}
