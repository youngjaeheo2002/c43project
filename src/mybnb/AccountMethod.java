package mybnb;

import javax.swing.plaf.nimbus.State;
import javax.swing.text.SimpleAttributeSet;
import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;

public class AccountMethod extends Methods{

    // Write operation related queries here

    //0 for success -1 for failure
    public int SignUp(String username, String password,String last_name, String first_name,String dob, String address,String occupation,int sin){
        try{
           
            PreparedStatement s = this.connection.prepareStatement("INSERT INTO accounts (username,password,last_name,first_name,dob,address,occupation,sin) VALUES ( ?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            s.setString(1,username);
            s.setString(2,password);
            s.setString(3,last_name);
            s.setString(4,first_name);
            s.setString(5,dob);
            s.setString(6,address);
            s.setString(7,occupation);
            s.setInt(8,sin);
            s.executeUpdate();

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
            PreparedStatement s = connection.prepareStatement("SELECT * FROM accounts WHERE username = ? AND password = ?", Statement.RETURN_GENERATED_KEYS);
            s.setString(1,username);
            s.setString(2,password);
        

            ResultSet result = s.executeQuery();

            return result.next();
        }

        catch(Exception e){
            System.out.println(e);
            return false;
        }

        finally{
            System.out.println("Login complete");
            
        }
    }

    public String getUsernameFromId(int uid) {
        String query = "SELECT username FROM accounts WHERE uid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, uid);
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                System.out.println("Failed to retrieve username of user " + uid);
                return null;
            }
            return result.getString("username");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when retrieving username of user " + uid);
            return null;
        }
    }

    public int getIdFromUsername(String username) {
        String query = "SELECT uid FROM accounts WHERE username = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                System.out.println("Failed to retrieve uid of user " + username);
                return 0;
            }
            return result.getInt("uid");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when retrieving uid of user " + username);
            return 0;
        }
    }


    public ResultSet getCreditCards(String username) throws Exception{
        try{
            PreparedStatement s = connection.prepareStatement("SELECT * FROM creditCards c, accounts a WHERE a.uid = c.renterId AND a.username = ?");
            s.setString(1,username);
            ResultSet result = s.executeQuery();
            return result;
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void addCreditCard(long card_num, String card_type, String expiry_date, int renterId) throws Exception{
        try{
            PreparedStatement s= connection.prepareStatement("INSERT INTO creditCards (card_num,card_type,expiry_date,renterId) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            s.setLong(1,card_num);
            s.setString(2,card_type);
            s.setString(3,expiry_date);
            s.setInt(4,renterId);
            s.executeUpdate();
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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE username = ?",Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,username);
            ResultSet r = statement.executeQuery();
            return r;
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public boolean isaHost(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM listings WHERE hostId = ?",Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,id);
            ResultSet result = statement.executeQuery();

            PreparedStatement s = connection.prepareStatement("SELECT * FROM creditcards WHERE renterID = ?",Statement.RETURN_GENERATED_KEYS);
            s.setInt(1,id);
            ResultSet r = s.executeQuery();
            return result.next() && r.next();


        }

        catch(Exception e){
            System.out.println(e);
        }

        return false;
    }

    public boolean isaRenter(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM creditCards WHERE renterId = ?", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,id);
            return statement.executeQuery().next();
        }

        catch(Exception e){
            System.out.println(e);
            return false;

        }

        finally{
            System.out.println("found if id is a renter");
        }

        
    }

    public ResultSet getBookings(int id){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM bookings WHERE renterId = ?",Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,id);
            ResultSet r =  statement.executeQuery();
            while(r.next()){
                String s = "|";

                for (int i = 1;i<=7;i++){
                    s += r.getString(i) + "|";
                }
                System.out.println(s);
            }
            return r;

        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Selected bookings");
        }

    }

    public ResultSet getListings(int id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM listings WHERE hostId = ?",Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,id);
            ResultSet r =  statement.executeQuery();
            while(r.next()){
                String s = "|";

                for (int i = 1;i<=10;i++){
                    s += r.getString(i) + "|";
                }
                System.out.println(s);
            }
            return r;
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Selected listings");
        }

    }

    public void addComment(String comment, int rating, int sender, int receiver, int on_listing ){
        try{
            PreparedStatement s = connection.prepareStatement("INSERT INTO comments (comment,rating,sender,receiver,on_listing) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            s.setString(1,comment);
            s.setInt(2,rating);
            s.setInt(3,sender);
            s.setInt(4,receiver);
            s.setInt(5,on_listing);
            s.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        finally {
            System.out.println("Successfully added comment");
        }
    }

    public void deleteComment(int cid){
        try{
            PreparedStatement s= connection.prepareStatement("DELETE FROM comments WHERE commentID = ?",Statement.RETURN_GENERATED_KEYS);
            s.setInt(1,cid);
            s.executeUpdate();
        }

        catch(Exception e){
            System.out.println(e);
        }

        finally{
            System.out.println("Successfully deleted comment");
        }
    }

    public ResultSet getCommentsFrom(int sender){
        try{
            PreparedStatement c = connection.prepareStatement("SELECT * FROM comments WHERE sender = ?",Statement.RETURN_GENERATED_KEYS);
            c.setInt(1,sender);
            ResultSet r =  c.executeQuery();
            while(r.next()){
                String s = "|";

                for (int i = 1;i<=6;i++){
                    s += r.getString(i) + "|";
                }
                System.out.println(s);
            }
            return r;
        }

        catch(Exception e){
            System.out.println(e);
            return null;

        }

        finally{
            System.out.println("Successfully searched for comments from ");
        }
    }

    public ResultSet getCommentsAbout(int receiver){
        try{
            PreparedStatement c = connection.prepareStatement("SELECT * FROM comments WHERE receiver = ?",Statement.RETURN_GENERATED_KEYS);
            c.setInt(1,receiver);
            ResultSet r =  c.executeQuery();
            while(r.next()){
                String s = "|";

                for (int i = 1;i<=6;i++){
                    s += r.getString(i) + "|";
                }
                System.out.println(s);
            }
            return r;
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Got comments about");
        }
    }

    public ResultSet getCommentsAboutListing(int listing){
        try{
            PreparedStatement c= connection.prepareStatement("SELECT * FROM comments WHERE on_listing = ?",Statement.RETURN_GENERATED_KEYS);
            c.setInt(1,listing);
            ResultSet r =  c.executeQuery();
            while(r.next()){
                String s = "|";

                for (int i = 1;i<=6;i++){
                    s += r.getString(i) + "|";
                }
                System.out.println(s);
            }
            return r;
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("got commetns about listing");
        }
    }

    public ResultSet getAvgRatingById(){
        try{
            PreparedStatement c = connection.prepareStatement("SELECT avg(rating), receiver FROM comments GROUP BY receiver");
            ResultSet r = c.executeQuery();
            while(r.next()){
                String s = "|";

                for (int i = 1;i<=2;i++){
                    s += r.getString(i) + "|";
                }
                System.out.println(s);
            }
            return r;
        }

        catch (Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Got avg rating by id");
        }



    }







}
