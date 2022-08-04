package mybnb;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;

public class Searches extends Methods{

    public ResultSet searchWithinDistance(double latitude,double longitude,double distance){
        try{
            PreparedStatement s = connection.prepareStatement("SELECT * FROM listings WHERE SQRT(POWER((latitude-?),2) + POWER((longitude-?),2)) <= ?  ORDER BY SQRT(POWER((latitude-?),2) + POWER((longitude-?),2)) ASC" ,Statement.RETURN_GENERATED_KEYS);
            s.setDouble(1,latitude);
            s.setDouble(2,longitude);
            s.setDouble(3,distance);
            s.setDouble(4,latitude);
            s.setDouble(5,longitude);
            return s.executeQuery();
        }

        catch (Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Completed Search Within Distance ");
        }
    }

    public ResultSet searchByPostalCode(String postal_code){
        try{
            String like_string = postal_code.substring(0,3);
            like_string = like_string + "%";
            PreparedStatement s = connection.prepareStatement("SELECT l.* FROM listings l, addresses a, at b" +
                    " WHERE l.lid = b.listing AND b.address = a.aid" +
                    " AND a.postal LIKE ?",Statement.RETURN_GENERATED_KEYS);
            s.setString(1,like_string);
            return s.executeQuery();
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Completed search by postal code");
        }
    }

    public ResultSet searchByExactAddress(String country,String city,String street_address){
        try{
            PreparedStatement s = connection.prepareStatement("SELECT l.* FROM listings l, addresses a, at b" +
                    " WHERE l.lid = b.listing AND b.address = a.aid" +
                    " AND a.country = ? AND a.city = ? AND a.street_address = ?",Statement.RETURN_GENERATED_KEYS);
            s.setString(1,country);
            s.setString(2,city);
            s.setString(3,street_address);
            return s.executeQuery();
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Completed search by exact address");
        }

    }

    public ResultSet temporalSearch( String start, String end){
        try{
            PreparedStatement s = connection.prepareStatement("SELECT l.* FROM listings l, available_on a" +
                    " WHERE a.lid = l.lid" +
                    " AND a.date <= ? AND a.date >= ?",Statement.RETURN_GENERATED_KEYS);
            s.setString(1,end);
            s.setString(2,start);
            return s.executeQuery();
        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Finished searchign by time");
        }
    }



    // Write search related queries here
}
