package dataObjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class Listing {
    public int lid;
    public int host;
    public String type;
    public String title;
    public String desc;
    public int num_bedrooms;
    public int num_bathrooms;
    public double price;
    public double latitude;
    public double longitude;

    public Listing() {

    }

    public Listing(int host, String type, String title, String desc, int bedrooms, int bathrooms, double price, double latitude, double longitude) {
        this.host = host;
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.num_bathrooms = bathrooms;
        this.num_bedrooms = bedrooms;
        this.price = price;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static ArrayList<Listing> buildListingArray(ResultSet rs) throws SQLException {
        ArrayList<Listing> listingArr = new ArrayList<>();
        int columns = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Listing listing = new Listing();
            for (int i = 1; i <= columns; i ++) {
                switch (rs.getMetaData().getColumnLabel(i).toLowerCase()) {
                    case "lid" -> listing.lid = rs.getInt(i);
                    case "listing_type" -> listing.type = rs.getString(i);
                    case "title" -> listing.title = rs.getString(i);
                    case "description" -> listing.desc = rs.getString(i);
                    case "num_bedrooms" -> listing.num_bedrooms = rs.getInt(i);
                    case "num_bathrooms" -> listing.num_bathrooms = rs.getInt(i);
                    case "price" -> listing.price = rs.getDouble(i);
                    case "hostid" -> listing.host = rs.getInt(i);
                    case "longitude" -> listing.longitude = rs.getDouble(i);
                    case "latitude" -> listing.latitude = rs.getDouble(i);
                    default -> {
                    }
                }
            }
            listingArr.add(listing);
        }
        return listingArr;
    }
}