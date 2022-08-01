package mybnb;

import java.sql.*;
import java.util.ArrayList;

public class AccountMethod extends Methods{

    // Write operation related queries here

    //0 for success -1 for failure
    public int SignUp(String username, String password,String last_name, String first_name,Date dob, String address,String occupation,int sin){
        try{
           
            PreparedStatement account = connection.prepareStatement("INSERT INTO accounts (username,password,last_name,first_name,dob,address,occupation,sin) VALUES ( '"+username+"','"+password+"','"+last_name+"','"+first_name+"',,'"+dob+"''"+address+"', '"+occupation+"', '"+sin+"')");
            account.executeUpdate();

        }

        catch(Exception e){
            System.out.println(e);
            return -1;
        }
        finally{
            System.out.println("SignUp Completed");
        }
        return 0;
    }

    public boolean Login(String username,String password) throws Exception{
        try{
            PreparedStatement account = connection.prepareStatement("SELECT * FROM accounts WHERE username = '"+username+"' AND password = '"+password+"'");
        

            ResultSet result = account.executeQuery();
            ArrayList<String> array = new ArrayList<String>();
            while(result.next() ){
                array.add (result.getString("username"));
               
            }
            System.out.println("Login complete");
            return !(array.isEmpty());
        }

        catch(Exception e){
            System.out.println(e);
            return false;
        }

        finally{
            
        }
    }

    public ResultSet getCreditCards(String username) throws Exception{
        try{
            PreparedStatement creditCard = connection.prepareStatement("SELECT c.cardId, c.card_num, c.expiry_date, c.card_type FROM creditCards c, accounts a WHERE a.uid = c.renterId AND a.username = '"+username+"'");
            ResultSet result = creditCard.executeQuery();
            
            return result;
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("All credit cards have been selected");
        }

    }

    public void addCreditCard(int card_num, String card_type,Date expiry_date, int renterId) throws Exception{
        try{
            PreparedStatement statement = connection.prepareStatement("INSERT INTO creditCards (card_num,card_type,expiry_date,renterId) VALUES ('"+card_num+"','"+card_type+"','"+expiry_date+"','"+renterId+"')");
            
            statement.executeUpdate();
        }

        catch(Exception e){
            System.out.println(e);

        }

        finally{
            System.out.println("Credit Card added");
        }
    }

    public ResultSet getAccountInfo(String username){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE username = '"+username+"'");
            return statement.executeQuery();
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Got Account info");
        }
    }

    public boolean isaHost(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM listings WHERE hostId = '"+id+"'");
            ResultSet result = statement.executeQuery();
            return result.next();
        }

        catch(Exception e){
            System.out.println(e);
        }

        return false;
    }

    public boolean isaRenter(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM creditCards WHERE renterId = '"+id+"'");
            return statement.executeQuery().next();
        }

        catch(Exception e){
            System.out.println(e);

        }
         return false;
        
    }

    public ResultSet getBookings(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM bookings WHERE renterId = '"+id+"'");
            return statement.executeQuery();
        }

        catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    public ResultSet getListings(int id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM listings WHERE hostId = '"+id+"'");
            return statement.executeQuery();
        }

        catch(Exception e){
            System.out.println(e);
        }
        return null;
    }


}
