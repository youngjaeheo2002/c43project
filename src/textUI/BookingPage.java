package textUI;

import mybnb.*;
import dataObjects.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.sql.*;

public class BookingPage {
    public int uid;
    public BookingMethods bookingMethods;
    public AccountMethod accountMethod;
    public Scanner inputScanner;

    public BookingPage(int uid) {
        this.uid = uid;
        this.bookingMethods = new BookingMethods();
        this.accountMethod = new AccountMethod();
        this.inputScanner = new Scanner(System.in);
    }

    public void displayBookings(int id, boolean is_renter, boolean is_cancelled) {
        ArrayList<Booking> bookings;
        if (is_renter) {
            bookings = bookingMethods.getRenterBookings(id);
        } else {
            bookings = bookingMethods.getListingBookings(id);
        }
        for (Booking b: bookings) {
            if (b.cancelled == is_cancelled) {
                System.out.println(b.toString());
                if (is_renter) {
                    System.out.println("  listing: " + b.listing);
                } else {
                    System.out.println("  renter: " + b.renter);
                }
                if (is_cancelled) {
                    try {
                        ResultSet cancellation = bookingMethods.getCancellationOfBooking(b.bid);
                        cancellation.next();
                        System.out.println("  Cancelled by: " + cancellation.getInt("canceller"));
                        System.out.println("  Cancelled on: " + cancellation.getDate("cancel_date"));

                    } catch (SQLException e) {
                        System.out.println("... Error when retrieving cancellation of booking " + b.bid);
                    }
                }
                System.out.println("");
            }
        }
    }

    public void displayOptions(int id, boolean is_renter) {
        boolean exit = false;
        String optionFormat;
        while (!exit) {
            if (is_renter) {
                System.out.println("Your bookings: ");
                optionFormat = "listing";
            } else {
                System.out.println("Booking of listing " + id + ": ");
                optionFormat = "renter";
            }
            displayBookings(id, is_renter, false);
            String optionString = "\nBooking options:\n    1. Cancel booking\n    2. View cancelled bookings\n    3. Comment on " + optionFormat + " in booking\n    4. Return\nChoose your option: ";
            System.out.print(optionString);
            String userInput = inputScanner.nextLine();
            switch (userInput) {
                case "1":
                    cancelBooking();
                    break;
                case "2":
                    if (is_renter) {
                        System.out.println("\nDisplaying account " + this.uid + " cancelled bookings: ");
                    } else{
                        System.out.println("\nDisplaying cancelled bookings of listing " + id + ":");
                    }
                    displayBookings(id, is_renter, true);
                    System.out.print("Go back - [Enter]");
                    inputScanner.nextLine();
                    break;
                case "3":
                    makeComment(is_renter);
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown option");
            }
        }
    }

    public void cancelBooking() {
        System.out.print("Enter the booking id to cancel: ");
        int bid = Integer.parseInt(inputScanner.nextLine());
        System.out.println("Are you sure you want to cancel the booking? (y/n) ");
        String userConfirm = inputScanner.nextLine();
        if (userConfirm.toLowerCase().equals("y")) {
            System.out.println("Cancelling booking " + bid + "...");
            bookingMethods.cancelBooking(this.uid, bid);
        } else {
            System.out.println("Cancel option...");
        }
    }

    public void makeComment(boolean is_renter) {
        System.out.print("Choose a booking id to comment on: ");
        int bid = Integer.parseInt(inputScanner.nextLine());

        Boolean exist = bookingMethods.exists(bid);
        if (exist == null || !exist) {
            System.out.println("Booking " + bid + " does not exist.");
            return;
        }

        Boolean cancelled = bookingMethods.isCancelled(bid);
            if (cancelled == null || cancelled) {
                System.out.println("Booking " + bid + " was cancelled.");
                return;
            }

        Booking booking = bookingMethods.getBookingById(bid);
        int hostId = bookingMethods.getHostByBooking(bid);

        String format;
        if (is_renter) {
            format = "listing";
            if (booking.renter != this.uid) {
                System.out.println("You cannot comment on this listing because you have not rented this listing.");
                return;
            }
        } else {
            format = "renter";
            if (hostId != this.uid) {
                System.out.println("You are not the host of this listing");
                return;
            }
        }

        System.out.print("Enter a rating for this " + format + ": ");
        int rating = Integer.parseInt(inputScanner.nextLine());
        if (rating < 0 || rating > 5) {
            System.out.println("Rating must be from 0-5");
            return;
        }

        System.out.println("Type a comment you want to leave for this " + format + ": ");
        String comment = inputScanner.nextLine();
        if (is_renter) {
            accountMethod.addComment(comment, rating, this.uid, hostId, booking.listing, true);
        } else {
            accountMethod.addComment(comment, rating, this.uid, booking.renter, booking.listing, false);
        }

    }
}
