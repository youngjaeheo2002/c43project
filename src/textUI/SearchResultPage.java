package textUI;

import mybnb.*;
import dataObjects.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;
import java.time.LocalDate;

public class SearchResultPage {
    public ArrayList<Listing> searchResults;
    public String order;
    public int uid;
    public ListingMethods listingMethods;
    public BookingMethods bookingMethods;
    public Scanner inputScanner;

    public SearchResultPage(ArrayList<Listing> result, int uid) {
        this.searchResults = result;
        this.uid = uid;
        this.listingMethods = new ListingMethods();
        this.bookingMethods = new BookingMethods();
        this.inputScanner = new Scanner(System.in);
    }

    public void displayResult() {
        System.out.println("Search results:");
        for (Listing l: this.searchResults) {
            System.out.println(l.toString());
        }
    }

    public void displayOptions() {
        boolean exit = false;
        while (!exit) {
            displayResult();
            System.out.print("\nSearch Options:\n    1. See a listing\n    2. Sort listings\n    3. Return\nChoose an option: ");
            String userOption = inputScanner.nextLine();
            switch (userOption) {
                case "1":
                    seeListing();
                    break;
                case "2":

                    break;
                case "3":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown option");
            }
        }
    }

    public void seeListing() {
        System.out.print("Enter the id of the listing you would like to see: ");
        int lid = Integer.parseInt(inputScanner.nextLine());
        Listing viewListing = listingMethods.getListingById(lid);
        System.out.println(viewListing.toStringFull());
        System.out.println(viewListing.showCoords());
        System.out.println(viewListing.showAvailability());
        System.out.println("\nDo you want to book this listing? (y/n)");
        String userChoice = inputScanner.nextLine();
        if (userChoice.equals("y") || userChoice.equals("Y")) {
            System.out.println("Booking listing " + lid + "...");
            bookListing(lid);
        } else {
            System.out.println("Return to search page...");
        }
    }

    public void bookListing(int lid) {
        Listing bookListing = listingMethods.getListingById(lid);
        System.out.print("Enter the start date (YYYY-MM-DD): ");
        Date start = Date.valueOf(LocalDate.parse(inputScanner.nextLine()));
        System.out.print("Enter the end date (YYYY-MM-DD): ");
        Date end = Date.valueOf(LocalDate.parse(inputScanner.nextLine()));
        Booking newBooking = new Booking(lid, this.uid, start, end, bookListing.price);
        bookingMethods.createBooking(newBooking);
    }
}
