package dataObjects;

import java.sql.*;
import java.util.ArrayList;

public class Booking {
    public int bid;
    public Date book_date;
    public Date start;
    public Date end;
    public double cost;
    public boolean cancelled;
    public int listing;
    public int renter;

    public Booking() {

    }

    public Booking(int listing, int renter, Date start, Date end, double cost) {
        this.listing = listing;
        this.renter = renter;
        this.start = start;
        this.end = end;
        this.cost = cost;
        this.cancelled = false;
    }

    public static ArrayList<Booking> buildBookingArray(ResultSet rs) throws SQLException {
        ArrayList<Booking> bookingArr = new ArrayList<>();
        int columns = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Booking booking = new Booking();
            for (int i = 1; i <= columns; i ++) {
                switch (rs.getMetaData().getColumnLabel(i).toLowerCase()) {
                    case "bid" -> booking.bid = rs.getInt(i);
                    case "book_date" -> booking.book_date = rs.getDate(i);
                    case "start_date" -> booking.start = rs.getDate(i);
                    case "end_date" -> booking.end = rs.getDate(i);
                    case "cost" -> booking.cost = rs.getDouble(i);
                    case "is_cancelled" -> booking.cancelled = rs.getBoolean(i);
                    case "listing" -> booking.listing = rs.getInt(i);
                    case "renterid" -> booking.renter = rs.getInt(i);
                    default -> {}
                }
            }
            bookingArr.add(booking);
        }
        return bookingArr;
    }
}
