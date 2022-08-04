package textUI;

import mybnb.*;
import dataObjects.*;

import java.util.ArrayList;

public class BookingPage {
    public BookingMethods bookingMethods;

    public BookingPage() {
        this.bookingMethods = new BookingMethods();
    }

    public void displayBookings(int id, boolean is_renter) {
        ArrayList<Booking> bookings;
        if (is_renter) {
            bookings = bookingMethods.getRenterBookings(id);
        } else {
            bookings = bookingMethods.getListingBookings(id);
        }
        for (Booking b: bookings) {
            if (!b.cancelled) {
                System.out.println(b.toString());
                if (is_renter) {
                    System.out.println("listing: " + b.listing);
                } else {
                    System.out.println("renter: " + b.renter);
                }
            }
        }
    }

    public void displayOptions(int id, boolean is_renter) {
        displayBookings(id, is_renter);
    }
}
