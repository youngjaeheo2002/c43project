package mybnb;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public ResultSet fullSearch(String opcode, double latitude, double longitude , double distance ,String postal_code,String country,String city,String street_address,String start , String end, ArrayList<String> amenities){
        try{
            String s = "SELECT l.* FROM listings l, available_on a, addresses d, has_amenities x, at b WHERE l.lid = a.lid AND l.lid = b.listing AND d.aid = b.address AND l.lid = x.lid ";
            if (opcode.charAt(0) == '1'){
                s += "AND SQRT(POWER((l.latitude-"+latitude+"),2) + POWER((l.longitude-"+longitude+"),2)) <= "+distance+" ";
            }

            if (opcode.charAt(1) == '1'){
                String like_string = postal_code.substring(0,3);
                like_string = like_string + "%";
                s += "AND d.postal LIKE  '"+like_string+"' ";
            }

            if (opcode.charAt(2) == '1'){
                s += "AND d.country = '"+country+"' AND d.city = '"+city+"' AND d.street_address = '"+street_address+"' ";
            }

            if (opcode.charAt(3) == '1'){
                s+= "AND a.date <= '"+end+"' AND a.date >= '"+start+"' ";
            }

            if (opcode.charAt(4) == '1'){
                s += "AND (";
                for (int i = 0;i<amenities.size();i++){
                    if (i == 0){
                        s += "x.amenity = '"+amenities.get(i)+"' ";
                    }
                    else{
                        s += "OR x.amenity = '"+amenities.get(i)+"' ";
                    }
                }
                s += ")";
            }
            System.out.println(s);

            PreparedStatement statement = this.connection.prepareStatement(s);
            return statement.executeQuery();

        }

        catch(Exception e){
            System.out.println(e);
            return null;
        }

        finally{
            System.out.println("Completed Full Search");
        }

    }



    // Write search related queries here
}
