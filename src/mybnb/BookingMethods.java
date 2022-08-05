package mybnb;

import dataObjects.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class BookingMethods extends Methods{
    public ListingMethods listingMethods;
    public BookingMethods() {
        super();
        listingMethods = new ListingMethods();
    }

    public int createBooking(Booking booking) {
        String query = "INSERT INTO bookings(start_date, end_date, cost, is_cancelled, book_date, listing, renterId) VALUES (?,?,?,?,?,?,?)";

        Boolean existence = listingMethods.exists(booking.listing);
        if (existence == null || !existence) {
            System.out.println("Listing " + booking.listing + " does not exist.");
            return -1;
        }
        Boolean validDate = listingMethods.availableDateRange(booking.listing, booking.start.toLocalDate(), booking.end.toLocalDate());
        if (validDate == null || !validDate) {
            System.out.println("Date range contains a date that is not available.");
            return -1;
        }

        Date now = Date.valueOf(LocalDate.now());
        if (booking.start.before(now)) {
            System.out.println("You cannot book dates in the past.");
        }

        try {
            PreparedStatement ps = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, booking.start);
            ps.setDate(2, booking.end);
            ps.setDouble(3, booking.cost);
            ps.setBoolean(4, booking.cancelled);
            ps.setDate(5, Date.valueOf(LocalDate.now()));
            ps.setInt(6, booking.listing);
            ps.setInt(7, booking.renter);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) {
                System.out.println("Failed to create a new booking.");
                return -1;
            }
            int bid = (int) keys.getLong(1);

            // Remove booked dates from availability
            for (Date i = booking.start; i.before(booking.end) || i.equals(booking.end); i = Date.valueOf(i.toLocalDate().plusDays(1))) {
                if (!listingMethods.removeAvailability(booking.listing, i)){
                    return -1;
                }
            }

            System.out.println("Successfully created booking " + bid);
            return bid;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when creating a new booking.");
            return -1;
        }
    }

    public Boolean validCanceller(int bid, int uid) {
        String query = "SELECT b.bid FROM bookings b, listings l WHERE b.listing = l.lid AND b.bid = ? AND (b.renterId = ? OR l.hostId = ?)";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            ps.setInt(2, uid);
            ps.setInt(3, uid);
            ResultSet result = ps.executeQuery();
            return result.next();

        } catch (SQLException e) {
            System.out.println("Error occurred when checking User " + uid + " permission to edit booking " + bid);
            return null;
        }
    }

    public Boolean isCancelled(int bid) {
        String query = "SELECT * FROM bookings WHERE bid = ? AND is_cancelled = true";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            ResultSet result = ps.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking for cancel status of booking " + bid);
            return null;
        }
    }

    public Boolean isStarted(int bid) {
        String query = "SELECT start_date FROM bookings WHERE bid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            ResultSet result = ps.executeQuery();
            result.next();
            Date start_date = result.getDate("start_date");
            return start_date.before(Date.valueOf(LocalDate.now()));

        } catch (SQLException e) {
            System.out.println("Error occurred when retrieving started date of booking " + bid);
            return null;
        }
    }

    public Boolean exists(int bid) {
        String query = "SELECT * FROM bookings WHERE bid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            ResultSet result = ps.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking for existence of booking " + bid);
            return null;
        }

    }

    public boolean cancelBooking(int canceller, int bid) {
        String[] queries = {
                "UPDATE bookings SET is_cancelled = true WHERE bid = ?",
                "INSERT INTO cancellations (booking, canceller, cancel_date) VALUES (?, ?, ?)",
                "SELECT listing, start_date, end_date FROM bookings WHERE bid = ?"
        };

        Boolean exist = exists(bid);
        if (exist == null || !exist) {
            System.out.println("Booking " + bid + " does not exist.");
            return false;
        }

        Boolean isCancelled = isCancelled(bid);
        if (isCancelled == null || isCancelled) {
            System.out.println("Booking " + bid + " is already cancelled.");
            return false;
        }

        Boolean cancellerCheck = validCanceller(bid, canceller);
        if (cancellerCheck == null || !cancellerCheck) {
            System.out.println("User " + canceller + " not have permission to cancel booking " + bid);
            return false;
        }

        Boolean startedCheck = isStarted(bid);
        if (startedCheck == null || startedCheck) {
            System.out.println("Cannot cancel booking " + bid + "because it is already started");
            return false;
        }

        try {
            PreparedStatement ps1 = this.connection.prepareStatement(queries[0]);
            ps1.setInt(1, bid);
            ps1.executeUpdate();

            PreparedStatement ps2 = this.connection.prepareStatement(queries[1]);
            ps2.setInt(1, bid);
            ps2.setInt(2, canceller);
            ps2.setDate(3, Date.valueOf(LocalDate.now()));
            ps2.executeUpdate();

            PreparedStatement ps3 = this.connection.prepareStatement(queries[2]);
            ps3.setInt(1, bid);
            ResultSet booking = ps3.executeQuery();

            booking.next();
            int listing = booking.getInt("listing");
            Date start = booking.getDate("start_date");
            Date end = booking.getDate("end_date");

            for (Date i = start; i.before(end) || i.equals(end); i = Date.valueOf(i.toLocalDate().plusDays(1))) {
                if (!listingMethods.addAvailability(listing, i)){
                    return false;
                }
            }

            System.out.println("Successfully cancelled booking " + bid);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when cancelling booking " + bid);
            return false;
        }
    }

    public ArrayList<Booking> getRenterBookings(int renterId) {
        String query = "SELECT * FROM bookings WHERE renterId = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, renterId);
            ResultSet result = ps.executeQuery();
            return Booking.buildBookingArray(result);
        } catch (SQLException e) {
            System.out.println("Error occurred when retrieving bookings of renter " + renterId);
        }
        return null;
    }

    public ArrayList<Booking> getListingBookings(int lid) {
        String query = "SELECT * FROM bookings WHERE listing = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, lid);
            ResultSet result = ps.executeQuery();
            return Booking.buildBookingArray(result);
        } catch (SQLException e) {
            System.out.println("Error occurred when retrieving bookings of listing " + lid);
        }
        return null;
    }

    public ResultSet getCancellationOfBooking(int bid) {
        String query = "SELECT * FROM cancellations WHERE booking = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when retrieving cancellation of booking " + bid);
        }
        return null;
    }
    public Booking getBookingById(int bid) {
        String query = "SELECT * FROM bookings WHERE bid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            ResultSet result =  ps.executeQuery();
            if (!result.next()) {
                return null;
            }
            Booking booking = new Booking(result.getInt("listing"), result.getInt("renterId"), result.getDate("start_date"), result.getDate("end_date"), result.getDouble("cost"));
            booking.bid = result.getInt("bid");
            booking.book_date = result.getDate("book_date");
            booking.cancelled = result.getBoolean("is_cancelled");
            return booking;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when retrieving booking " + bid);
            return null;
        }
    }

    public int getHostByBooking(int bid) {
        String query = "SELECT l.hostId FROM bookings b, listings l WHERE b.listing = l.lid AND b.bid = ?";
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setInt(1, bid);
            ResultSet result =  ps.executeQuery();
            if (!result.next()) {
                return 0;
            }
            return result.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred when retrieving host from booking " + bid);
            return 0;
        }
    }
}
