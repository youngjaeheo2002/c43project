package dataObjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Listing {
    // Type constants
    public static final String ENTIRE_PLACE_TYPE = "Entire Place";
    public static final String HOTEL_ROOMS_TYPE = "Hotel Rooms";
    public static final String PRIVATE_ROOMS_TYPE = "Private Rooms";
    public static final String SHARED_ROOMS_TYPE = "Shared Rooms";
    public static final String OTHER_TYPE = "Other";
    public static final List<String> ALL_AMENITIES = Arrays.asList("Kitchen", "Internet", "TV", "Essentials", "Heating",
            "Air Conditioning", "Washer", "Dryer", "Free Parking", "Wireless",
            "Breakfast", "Pets", "Family Friendly", "Suitable for Events",
            "Smoking", "Wheelchair Accessible", "Elevator", "Fireplace", "Buzzer",
            "Doorman", "Pool", "Hot Tub", "Gym", "24 Hours Check-In", "Hangers",
            "Iron", "Hair Dryer", "Laptop-friendly Workspace",
            "Carbon Monoxide Detector", "First Aid Kit", "Smoke Detector");

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

    public static int isValidAmenitiesArray(ArrayList<String> arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (!ALL_AMENITIES.contains(arr.get(i))) {
                return i;
            }
        }
        return -1;
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
