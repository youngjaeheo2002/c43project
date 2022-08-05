package mybnb;

import java.sql.*;

public class ReportMethods {
    public ReportMethods() {

    }

    public ResultSet countBookingsPerCity(Date start, Date end) {
        String query = "SELECT a.city AS city, COUNT(b.bid) AS booking_num " +
                "FROM addresses a LEFT JOIN at ON a.aid = at.address LEFT JOIN bookings b ON b.listing = at.listing " +
                "WHERE b.start_date >= ? AND b.start_date <= ? " +
                "GROUP BY a.city";
    }

    public ResultSet countBookingsPerPostal(Date start, Date end) {
        String query = "SELECT a.city AS city, a.postal AS postal, COUNT(b.bid) as booking_num " +
                "FROM addresses a LEFT JOIN at ON a.aid = at.address LEFT JOIN bookings b ON b.listing = at.listing " +
                "WHERE b.start_date >= ? AND b.start_date <= ? " +
                "GROUP BY a.city, a.postal";
    }

    public ResultSet countListingsPerCountry() {
        String query = "SELECT a.country AS country, COUNT(at.listing) AS listing_num " +
                "FROM at RIGHT JOIN addresses a ON at.address = a.aid " +
                "GROUP BY a.country";
    }

    public ResultSet countListingPerCity() {
        String query = "SELECT a.country AS country, a.city AS city, COUNT(at.listing) AS listing_num " +
                "FROM at RIGHT JOIN addresses a ON at.address = a.aid " +
                "GROUP BY a.country, a.city";
    }

    public ResultSet countListingPerPostal() {
        String query = "SELECT a.country AS country, a.city AS city, a.postal AS postal, COUNT(at.listing) AS listing_num " +
                "FROM at RIGHT JOIN addresses a ON at.address = a.aid " +
                "GROUP BY a.country, a.city, a.postal";
    }

    public ResultSet rankHostsByListingsPerCountry() {
        String query = "SELECT addr.country AS country, a.uid AS host, COUNT(l.lid) AS listing_num " +
                "FROM accounts a " +
                "LEFT JOIN creditCards c ON c.renterId = a.uid " +
                "LEFT JOIN listings l ON l.hostId = a.aid " +
                "LEFT JOIN (at JOIN addresses a ON at.address = a.aid) at ON at.listing = l.lid " +
                "WHERE c.card_num IS NULL " +
                "GROUP BY country, host " +
                "ORDER BY country, listing_num DESC";
    }

    public ResultSet rankHostsByListingsPerCity() {
        String query = "SELECT addr.country AS country, addr.city AS city a.uid AS host, COUNT(l.lid) AS listing_num " +
                "FROM accounts a " +
                "LEFT JOIN creditCards c ON c.renterId = a.uid " +
                "LEFT JOIN listings l ON l.hostId = a.aid " +
                "LEFT JOIN (at JOIN addresses a ON at.address = a.aid) at ON at.listing = l.lid " +
                "WHERE c.card_num IS NULL " +
                "GROUP BY country, city, host " +
                "ORDER BY country, city, listing_num";
    }

    public ResultSet potentialCommercialHostPerCity() {
        String query = "SELECT lph.country AS country, lph.city AS city, lph.host AS host, lph.listing_count AS listing_count, lpc.listing_count AS total_count, (lph.listing_count / lpc.listing_count) * 100 AS market_share\n" +
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
    }

    public ResultSet rankRenterByBookingPerCountry(Date start, Date end) {
        String query = "SELECT addr.country AS country, a.uid AS renter, COUNT(b.bid) AS booking_count\n" +
                "FROM accounts a LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "LEFT JOIN bookings b ON a.uid = b.renterid\n" +
                "LEFT JOIN listings l ON b.listing = l.lid\n" +
                "LEFT JOIN (at JOIN addresses addr ON at.address = addr.aid) ON at.listing = l.lid\n" +
                "WHERE c.card_num IS NOT NULL AND b.is_cancelled = false AND b.start_date >= ? AND b.start_date <= ? \n" +
                "GROUP BY country, renter\n" +
                "ORDER BY booking_count DESC";
    }

    public ResultSet rankRenterByBookingPerCity(Date start, Date end) {
        String query = "SELECT addr.country AS country, addr.city AS city, a.uid AS renter, COUNT(b.bid) AS booking_count\n" +
                "FROM accounts a LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "LEFT JOIN bookings b ON a.uid = b.renterid\n" +
                "LEFT JOIN listings l ON b.listing = l.lid\n" +
                "LEFT JOIN (at JOIN addresses addr ON at.address = addr.aid) ON at.listing = l.lid\n" +
                "WHERE c.card_num IS NOT NULL AND b.is_cancelled = false AND b.start_date >= ? AND b.start_date <= ? \n" +
                "AND a.uid IN (\n" +
                "    SELECT a.uid\n" +
                "    FROM accounts a LEFT JOIN creditCards c ON a.uid = c.renterId\n" +
                "    LEFT JOIN bookings b ON a.uid = b.renterid\n" +
                "    WHERE c.card_num IS NOT NULL AND b.is_cancelled = false AND b.start_date >= ? AND b.start_date <= ?\n" +
                "    GROUP BY a.uid\n" +
                "    HAVING COUNT(b.bid) > 1\n" +
                ")\n" +
                "GROUP BY country, city, renter\n" +
                "ORDER BY booking_count DESC";
    }

    public ResultSet rankUserByCancellation(String year) {
        String query = "SELECT a.uid AS user, COUNT(c.cid) AS cancellation_count \n" +
                "FROM accounts a LEFT JOIN cancellations c ON c.canceller = a.uid\n" +
                "WHERE c.cancel_date >= ? AND c.cancel_date <= ? \n" +
                "GROUP BY user\n" +
                "ORDER BY cancellation_count DESC";
    }

}
