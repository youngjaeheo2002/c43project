package mybnb;
import dataObjects.*;

//import java.time.LocalDate;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ListingMethods extends Methods{
    public ListingMethods() {
        super();
    }

    public boolean addListing(Listing listing) {
        String query = "INSERT INTO listings(hostId, title, description, listing_type, num_bedrooms, num_bathrooms, longitude, latitude, price) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, listing.host);
            ps.setString(2, listing.title);
            ps.setString(3, listing.desc);
            ps.setString(4, listing.type);
            ps.setInt(5, listing.num_bedrooms);
            ps.setInt(6, listing.num_bathrooms);
            ps.setDouble(7, listing.longitude);
            ps.setDouble(8, listing.latitude);
            ps.setDouble(9, listing.price);
            int rows = ps.executeUpdate();
            if (rows != 0) {
                System.out.println("Successfully added listing.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error occurred when adding listing.");
        }
        return false;
    }

    public boolean updatePrice(int hostId, int listingId, double newPrice) {
        String query = "UPDATE listings SET price = ? WHERE hostId = ? AND lid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setDouble(1, newPrice);
            ps.setInt(2, hostId);
            ps.setInt(3, listingId);
            int rows = ps.executeUpdate();
            if (rows != 0) {
                System.out.println("Successfully updated price for listing " + listingId);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error occurred when updating listing price.");
        }
        return false;
    }

    public boolean addAvailability(int lid, Date date) {
        String[] queries = {
                "SELECT bid FROM bookings WHERE ? >= start_date AND ? <= end_date",
                "SELECT * FROM available_on WHERE lid = ? AND date = ?",
                "INSERT INTO available_on(lid, date) VALUES(?, ?)"
        };
        try {
            // Check if the date is already booked
            PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
            ps1.setDate(1, date);
            ps1.setDate(2, date);
            ResultSet bookedCheck = ps1.executeQuery();
            if (bookedCheck.next()) {
                System.out.println("Date " + date + " is already booked by booking " + bookedCheck.getObject(1));
                return false;
            }
            // Check if the date is already available.
            PreparedStatement ps2 = this.connection.prepareStatement(queries[1]);
            ps2.setInt(1, lid);
            ps2.setDate(2, date);
            ResultSet availableCheck = ps2.executeQuery();
            if (availableCheck.next()) {
                System.out.println("Date " + date + " is already available.");
                return false;
            }
            // Add date to available_on
            PreparedStatement ps3 = this.connection.prepareStatement(queries[2]);
            ps3.setInt(1, lid);
            ps3.setDate(2, date);
            int rows =  ps3.executeUpdate();
            if (rows != 0) {
                System.out.println("Successfully added date " + date);
                return true;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error occurred when setting availability of date " + date);
        }
        return false;
    }

    public boolean removeAvailability(int lid, Date date) {
        String[] queries = {
                "SELECT * FROM available_on WHERE lid = ? AND date = ?",
                "DELETE FROM available_on WHERE lid = ? AND date = ?"
        };
        try {
            // Check if date is currently available
            PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
            ps1.setInt(1, lid);
            ps1.setDate(2, date);
            ResultSet availabilityCheck = ps1.executeQuery();
            if (!availabilityCheck.next()) {
                System.out.println("The date " + date + " is currently not available.");
                return false;
            }
            // Remove date
            PreparedStatement ps2 = this.connection.prepareStatement(queries[1]);
            ps2.setInt(1, lid);
            ps2.setDate(2, date);
            int rows =  ps2.executeUpdate();
            if (rows != 0) {
                System.out.println("Successfully removed date " + date);
                return true;
            }
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Error occurred when removing date " + date);
        }
        return false;
    }

    public boolean removeListing(int hostId, int lid) {
        String[] queries = {
                "SELECT bid FROM bookings WHERE listing = ? AND start_date > ?",
                "DELETE FROM listings WHERE lid = ? AND hostId = ?"
        };
        try {
            LocalDate currDate = LocalDate.now();
            PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
            ps1.setInt(1, lid);
            ps1.setDate(2, Date.valueOf(currDate));
            ResultSet ongoingCheck = ps1.executeQuery();
            if (ongoingCheck.next()) {
                System.out.println("Cannot remove this listing because there is a booking in a future date.");
                return false;
            }

            PreparedStatement ps = this.connection.prepareStatement(queries[1]);
            ps.setInt(1, lid);
            ps.setInt(2, hostId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("This listing is does not exist.");
                return false;
            }
            System.out.println("Successfully removed listing.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error occurred when removing listing.");
        }
        return false;
    }

    public ArrayList<Listing> getListingsOfUser(int hostId) {
        String query = "SELECT * FROM listings WHERE hostId = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, hostId);
            ResultSet result = ps.executeQuery();
            return Listing.buildListingArray(result);
        } catch (SQLException e) {
            System.out.println("Error occurred when retrieving listings of host " + hostId);
        }
        return null;
    }
}
