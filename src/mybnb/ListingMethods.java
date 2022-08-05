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

    public Boolean exists(int lid) {
        String query = "SELECT * FROM listings WHERE lid = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, lid);
            ResultSet result = ps.executeQuery();
            return result.next();

        } catch (SQLException e) {
            System.out.println("Error occurred when checking for existence of listing " + lid);
            return null;
        }
    }

    public Boolean availableDateRange(int lid, LocalDate start, LocalDate end) {
        String query = "SELECT * FROM available_on WHERE lid = " + lid + " AND date = ?";

        if (end.isBefore(start)) {
            System.out.println("start date of " + start + "is after end date of " + end);
            return false;
        }

        try {
            for (LocalDate i = start; i.isBefore(end) || i.isEqual(end);  i = i.plusDays(1)) {
                PreparedStatement ps = this.connection.prepareStatement(query);
                ps.setDate(1, Date.valueOf(i));
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error occurred when checking for date range from " + start + " to " + end);
            return null;
        }
    }

    public int addListing(Listing listing) {
        String[] queries = {
                "INSERT INTO listings(hostId, title, description, listing_type, num_bedrooms, num_bathrooms, longitude, latitude, price, posted_date) VALUES (?,?,?,?,?,?,?,?,?,?)"
        };
        try {
            PreparedStatement ps = this.connection.prepareStatement(queries[0], Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, listing.host);
            ps.setString(2, listing.title);
            ps.setString(3, listing.desc);
            ps.setString(4, listing.type);
            ps.setInt(5, listing.num_bedrooms);
            ps.setInt(6, listing.num_bathrooms);
            ps.setDouble(7, listing.longitude);
            ps.setDouble(8, listing.latitude);
            ps.setDouble(9, listing.price);
            ps.setDate(10, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) {
                System.out.println("Failed to add new listing.");
                return -1;
            }
            int lid = (int) keys.getLong(1);


            System.out.println("Successfully created listing " + lid);
            return lid;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when adding listing.");
        }
        return -1;
    }

    public String getHost(int lid) {
        String query = "SELECT hostId FROM listings WHERE lid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, lid);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getString("hostId");
            }
        } catch (SQLException e) {
            System.out.println("Failed to lookup listing or host.");
        }
        return null;
    }

    public boolean setAddress(int lid, Address addr) {
        String[] queries = {
                "SELECT aid FROM addresses WHERE country = ? AND city = ? AND street_address = ? AND postal = ?",
                "INSERT INTO addresses(country, city, street_address, postal) VALUES (?,?,?,?)",
                "INSERT INTO at(listing, address) VALUES (?, ?)"
        };

        Boolean lidExist = exists(lid);
        if (lidExist == null || !lidExist) {
            System.out.println("The listing " + lid + " does not exist.");
            return false;
        }

        try  {
            int aid = 0;
            // Check if the address is already exist
            PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
            ps1.setString(1, addr.country);
            ps1.setString(2, addr.city);
            ps1.setString(3, addr.street);
            ps1.setString(4, addr.postal);
            ResultSet existence = ps1.executeQuery();
            if (existence.next()) {
                aid = existence.getInt("aid");
            } else {
                // add the address if it does not exist.
                PreparedStatement ps2 = this.connection.prepareStatement(queries[1], Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1, addr.country);
                ps2.setString(2, addr.city);
                ps2.setString(3, addr.street);
                ps2.setString(4, addr.postal);
                ps2.executeUpdate();
                ResultSet keys = ps2.getGeneratedKeys();
                if (!keys.next()) {
                    System.out.println("Failed to add the new address.");
                    return false;
                }
                aid = (int) keys.getLong(1);
            }
            // Link the listing and the address
            PreparedStatement ps3 = this.connection.prepareStatement(queries[2]);
            ps3.setInt(1, lid);
            ps3.setInt(2, aid);
            int rows = ps3.executeUpdate();
            if (rows > 0) {
                System.out.println("Successfully updated address of listing " + lid);
                return true;
            }
            System.out.println("Failed to update address of listing " + lid);
            return false;

        } catch (SQLException e) {
            System.out.println("Error occurred when updating address of listing " + lid);
            return false;
        }
    }

    public boolean addAmenities(int lid, ArrayList<String> amenities) {
        String[] queries = {
                "SELECT * FROM has_amenities WHERE lid = ? AND amenity = ?",
                "INSERT INTO has_amenities(lid, amenity) VALUES (?, ?)"
        };

        Boolean lidExist = exists(lid);
        if (lidExist == null || !lidExist) {
            System.out.println("The listing " + lid + " does not exist.");
            return false;
        }

        int amenitiesCheck = Listing.isValidAmenitiesArray(amenities);
        if (amenitiesCheck != -1) {
            System.out.println("Amenity " + amenities.get(amenitiesCheck) + " is not supported.");
            return false;
        }

        try {
            for (String amenity : amenities) {
                PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
                ps1.setInt(1, lid);
                ps1.setString(2, amenity);
                ResultSet existence = ps1.executeQuery();
                if (!existence.next()) {
                    PreparedStatement ps2 = this.connection.prepareStatement(queries[1]);
                    ps2.setInt(1, lid);
                    ps2.setString(2, amenity);
                    ps2.executeUpdate();
                }
            }
            System.out.println("Successfully added amenities for listing " + lid);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when adding amenities, please try again.");
        }
        return false;
    }

    public boolean removeAmenities(int lid, ArrayList<String> amenities) {
        String[] queries = {
                "SELECT * FROM has_amenities WHERE lid = ? AND amenity = ?",
                "DELETE FROM has_amenities WHERE lid = ? AND amenity = ?"
        };

        Boolean lidExist = exists(lid);
        if (lidExist == null || !lidExist) {
            System.out.println("The listing " + lid + " does not exist.");
            return false;
        }

        int amenitiesCheck = Listing.isValidAmenitiesArray(amenities);
        if (amenitiesCheck != -1) {
            System.out.println("Amenity " + amenities.get(amenitiesCheck) + " is not supported.");
            return false;
        }

        try {
            for (String amenity : amenities) {
                PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
                ps1.setInt(1, lid);
                ps1.setString(2, amenity);
                ResultSet existence = ps1.executeQuery();
                if (existence.next()) {
                    PreparedStatement ps2 = this.connection.prepareStatement(queries[1]);
                    ps2.setInt(1, lid);
                    ps2.setString(2, amenity);
                    ps2.executeUpdate();
                }
            }
            System.out.println("Successfully removed amenities for listing " + lid);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when removing amenities, please try again.");
            return false;
        }
    }

    public boolean updatePrice(int hostId, int lid, double newPrice) {
        String query = "UPDATE listings SET price = ? WHERE hostId = ? AND lid = ?";

        Boolean lidExist = exists(lid);
        if (lidExist == null || !lidExist) {
            System.out.println("The listing " + lid + " does not exist.");
            return false;
        }

        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setDouble(1, newPrice);
            ps.setInt(2, hostId);
            ps.setInt(3, lid);
            int rows = ps.executeUpdate();
            if (rows != 0) {
                System.out.println("Successfully updated price of listing " + lid);
                return true;
            }
            System.out.println("Failed to update price of listing " + lid);

        } catch (SQLException e) {
            System.out.println("Error occurred when updating price of listing " + lid);
        }
        return false;
    }

    public boolean addAvailability(int lid, Date date) {
        String[] queries = {
                "SELECT bid FROM bookings WHERE ? >= start_date AND ? <= end_date AND is_cancelled = false",
                "SELECT * FROM available_on WHERE lid = ? AND date = ?",
                "INSERT INTO available_on(lid, date) VALUES(?, ?)"
        };

        Boolean lidExist = exists(lid);
        if (lidExist == null || !lidExist) {
            System.out.println("The listing " + lid + " does not exist.");
            return false;
        }

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
                // System.out.println("Successfully added date " + date);
                return true;
            }
            System.out.println("Failed to add date " + date + " to listing " + lid);

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error occurred when setting availability of listing " + lid);
        }
        return false;
    }

    public boolean removeAvailability(int lid, Date date) {
        String[] queries = {
                "SELECT * FROM available_on WHERE lid = ? AND date = ?",
                "DELETE FROM available_on WHERE lid = ? AND date = ?"
        };

        Boolean lidExist = exists(lid);
        if (lidExist == null || !lidExist) {
            System.out.println("The listing " + lid + " does not exist.");
            return false;
        }

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
                // System.out.println("Successfully removed date " + date);
                return true;
            }
            System.out.println("Failed to remove date " + date + " to listing " + lid);

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
                System.out.println("Cannot remove listing " + lid + " because there is a booking in a future date.");
                return false;
            }

            PreparedStatement ps = this.connection.prepareStatement(queries[1]);
            ps.setInt(1, lid);
            ps.setInt(2, hostId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("The listing " + lid + " does not exist.");
                return false;
            }
            System.out.println("Successfully removed listing " + lid);
            return true;
        } catch (SQLException e) {
            System.out.println("Error occurred when removing listing " + lid);
        }
        return false;
    }

    public ArrayList<Listing> getHostListings(int hostId) {
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

    public Listing getListingById(int lid) {
        String[] queries = {
                "SELECT * FROM listings WHERE lid = ?",
                "SELECT * FROM addresses addr, at WHERE addr.aid = at.address AND at.listing = ?",
                "SELECT amenity FROM has_amenities WHERE lid = ?",
                "SELECT date FROM available_on WHERE lid = ?"
        };

        try {
            PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
            ps1.setInt(1, lid);
            Listing listing = new Listing(ps1.executeQuery());
            if (listing.lid == 0) {
                System.out.println("Listing " + lid + " does not exist");
                return null;
            }

            PreparedStatement ps2 = this.connection.prepareStatement(queries[1]);
            ps2.setInt(1, lid);
            listing.setAddress(ps2.executeQuery());

            PreparedStatement ps3 = this.connection.prepareStatement(queries[2]);
            ps3.setInt(1, lid);
            listing.setAmenities(ps3.executeQuery());

            PreparedStatement ps4 = this.connection.prepareStatement(queries[3]);
            ps4.setInt(1, lid);
            listing.setAmenities(ps4.executeQuery());

            return listing;

        } catch (SQLException e) {
            System.out.println("Error occurred when retrieving listing " + lid);
            return null;
        }
    }
}
