package mybnb;

import java.sql.*;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;

public class ReportMethods extends Methods{
    final String BOOKINGS_PER_CITY = "SELECT a.country AS country,  a.city AS city, COUNT(b.bid) AS booking_num " +
                "FROM addresses a LEFT JOIN at ON a.aid = at.address " +
                "LEFT JOIN (SELECT * FROM bookings WHERE is_cancelled = false AND start_date >= ? AND start_date <= ?) b ON b.listing = at.listing " +
                "GROUP BY a.city";


    final String BOOKINGS_PER_POSTAL = "SELECT a.country AS country, a.city AS city, a.postal AS postal, COUNT(b.bid) as booking_num " +
                "FROM addresses a LEFT JOIN at ON a.aid = at.address " +
                "LEFT JOIN (SELECT * FROM bookings WHERE is_cancelled = false AND start_date >= ? AND start_date <= ?) b ON b.listing = at.listing " +
                "GROUP BY a.city, a.postal";

    final String LISTINGS_PER_COUNTRY = "SELECT a.country AS country, COUNT(at.listing) AS listing_num " +
                "FROM at RIGHT JOIN addresses a ON at.address = a.aid " +
                "GROUP BY a.country";

    final String LISTINGS_PER_CITY = "SELECT a.country AS country, a.city AS city, COUNT(at.listing) AS listing_num " +
                "FROM at RIGHT JOIN addresses a ON at.address = a.aid " +
                "GROUP BY a.country, a.city";

    final String LISTINGS_PER_POSTAL = "SELECT a.country AS country, a.city AS city, a.postal AS postal, COUNT(at.listing) AS listing_num " +
                "FROM at RIGHT JOIN addresses a ON at.address = a.aid " +
                "GROUP BY a.country, a.city, a.postal";

    final String HOST_LISTINGS_PER_COUNTRY = "SELECT addr.country AS country, a.uid AS hostId, COUNT(l.lid) AS listing_count " +
                "FROM accounts a " +
                "LEFT JOIN creditCards c ON c.renterId = a.uid " +
                "LEFT JOIN listings l ON l.hostId = a.uid " +
                "LEFT JOIN (at JOIN addresses addr ON at.address = addr.aid) ON at.listing = l.lid " +
                "WHERE c.card_num IS NULL " +
                "GROUP BY country, hostId " +
                "ORDER BY listing_count DESC";

    final String HOST_LISTINGS_PER_CITY = "SELECT addr.country AS country, addr.city AS city, a.uid AS hostId, COUNT(l.lid) AS listing_count " +
                "FROM accounts a " +
                "LEFT JOIN creditCards c ON c.renterId = a.uid " +
                "LEFT JOIN listings l ON l.hostId = a.uid " +
                "LEFT JOIN (at JOIN addresses addr ON at.address = addr.aid) ON at.listing = l.lid " +
                "WHERE c.card_num IS NULL " +
                "GROUP BY country, city, hostId " +
                "ORDER BY listing_count DESC";

    final String COMMERCIAL_HOSTS_PER_CITY = "SELECT lph.country AS country, lph.city AS city, lph.host AS hostId, lph.listing_count AS listing_count, lpc.listing_count AS total_count, (lph.listing_count / lpc.listing_count) * 100 AS market_share\n" +
                "FROM\n" +
                "(SELECT addr.country AS country, addr.city AS city, a.uid AS host, COUNT(l.lid) AS listing_count\n" +
                "    FROM accounts a\n" +
                "    LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "    LEFT JOIN listings l ON l.hostId = a.uid\n" +
                "    LEFT JOIN (at JOIN addresses addr ON at.address = addr.aid) ON at.listing = l.lid\n" +
                "    WHERE c.card_num IS NULL\n" +
                "    GROUP BY country, city) AS lph,\n" +
                "    (SELECT addr.country, addr.city, COUNT(l.lid) AS listing_count\n" +
                "    FROM listings l \n" +
                "    JOIN at ON at.listing = l.lid\n" +
                "    JOIN addresses addr ON at.address = addr.aid\n" +
                "    GROUP BY country, city) AS lpc\n" +
                "WHERE lph.country = lpc.country AND lph.city = lpc.city AND ((lph.listing_count / lpc.listing_count) * 100) >= 10";

    final String RENTER_BOOKINGS = "SELECT a.uid AS renterId, COUNT(b.bid) AS booking_count\n" +
                "FROM accounts a LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "LEFT JOIN (SELECT * FROM bookings WHERE is_cancelled = false AND start_date >= ? AND start_date <= ?) b ON a.uid = b.renterId\n" +
                "WHERE c.card_num IS NOT NULL\n"+
                "GROUP BY renterId\n" +
                "ORDER BY booking_count DESC";

    final String RENTER_BOOKINGS_PER_CITY = "SELECT addr.country AS country, addr.city AS city, a.uid AS renterId, COUNT(b.bid) AS booking_count\n" +
                "FROM accounts a LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "LEFT JOIN bookings b ON a.uid = b.renterId\n" +
                "LEFT JOIN listings l ON b.listing = l.lid\n" +
                "LEFT JOIN (at JOIN addresses addr ON at.address = addr.aid) ON at.listing = l.lid\n" +
                "WHERE c.card_num IS NOT NULL AND b.is_cancelled = false AND b.start_date >= ? AND b.start_date <= ? \n" +
                "AND a.uid IN (\n" +
                "    SELECT a.uid\n" +
                "    FROM accounts a LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "    LEFT JOIN bookings b ON a.uid = b.renterid\n" +
                "    WHERE c.card_num IS NOT NULL AND b.is_cancelled = false AND b.start_date >= ? AND b.start_date <= ?\n" +
                "    GROUP BY a.uid\n" +
                "    HAVING COUNT(b.bid) >= 2\n" +
                ")\n" +
                "GROUP BY country, city, renterId\n" +
                "ORDER BY booking_count DESC";

    final String USER_CANCELLATIONS = "SELECT a.uid AS userId, COUNT(c.cid) AS cancellation_count \n" +
                "FROM accounts a LEFT JOIN cancellations c ON c.canceller = a.uid\n" +
                "WHERE c.cancel_date >= ? AND c.cancel_date <= ? \n" +
                "GROUP BY userId\n" +
                "ORDER BY cancellation_count DESC";

    public ReportMethods() {

    }

    public ResultSet reportNoDate(String report) {
        String query;
        switch (report) {
            case "listings_country" -> query = LISTINGS_PER_COUNTRY;
            case "listings_city" -> query = LISTINGS_PER_CITY;
            case "listings_postal" -> query = LISTINGS_PER_POSTAL;
            case "host_country" -> query = HOST_LISTINGS_PER_COUNTRY;
            case "host_city" -> query = HOST_LISTINGS_PER_CITY;
            case "commercial_host" -> query = COMMERCIAL_HOSTS_PER_CITY;
            default -> {return null;}
        }
        // System.out.println(query);
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            return ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error when executing report query.");
            return null;
        }

    }

    public ResultSet reportDateRange(Date start, Date end, String report) {
        String query;
        switch(report) {
            case "bookings_city" -> query = BOOKINGS_PER_CITY;
            case "bookings_postal" -> query = BOOKINGS_PER_POSTAL;
            case "renter_ranking" -> query = RENTER_BOOKINGS;
            default -> {return null;}
        }
        try {
            PreparedStatement ps = this.connection.prepareStatement(query);
            ps.setDate(1, start);
            ps.setDate(2, end);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error when executing report query.");
            return null;
        }
    }

    public ResultSet getRenterBookingsPerCity(Date start, Date end) {
        Date year_start = Date.valueOf(LocalDate.parse("" + start.toLocalDate().getYear() + "-01-01"));
        Date year_end = Date.valueOf(year_start.toLocalDate().plusYears(1));

        try {
            PreparedStatement ps = this.connection.prepareStatement(RENTER_BOOKINGS_PER_CITY);
            ps.setDate(1, start);
            ps.setDate(2, end);
            ps.setDate(3, year_start);
            ps.setDate(4, year_end);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error when executing report query.");
            return null;
        }
    }

    public ResultSet getUserCancellation() {
        Date end = Date.valueOf(LocalDate.now());
        Date start = Date.valueOf(LocalDate.now().minusYears(1));

        try {
            PreparedStatement ps = this.connection.prepareStatement(USER_CANCELLATIONS);
            ps.setDate(1, start);
            ps.setDate(2, end);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error when executing report query.");
            return null;
        }
    }

}
