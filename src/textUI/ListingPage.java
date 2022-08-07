package textUI;

import mybnb.*;
import dataObjects.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class ListingPage {
    public int hostId;
    public CommentsPage commentsPage;
    public ListingMethods listingMethods;
    public Scanner inputScanner;
    public Listing currentListing;
    public BookingPage bookingPage;

    public ListingPage(int hostId) {
        this.hostId = hostId;
        this.commentsPage = new CommentsPage();
        this.listingMethods = new ListingMethods();
        this.inputScanner = new Scanner(System.in);
        this.bookingPage = new BookingPage(hostId);
    }

    public void displayListing(int lid) {
        this.currentListing = listingMethods.getListingById(lid);
        System.out.println(currentListing.toStringFull());
        System.out.println(currentListing.showCoords());
        System.out.println(currentListing.showAvailability());
    }

    public void displayOptions(int lid) {
        int realHost = listingMethods.getHost(lid);
        if (realHost != this.hostId) {
            System.out.println("You cannot access this listing.");
            return;
        }
        boolean exit = false;
        while (!exit) {
            displayListing(lid);
            System.out.print("Options:\n    1. Edit\n    2. View booking history\n    3. View listing comments\n    4. Return\nChoose an option: ");
            String userInput = inputScanner.nextLine();
            switch (userInput) {
                case "1":
                    displayEditOptions(lid);
                    break;
                case "2":
                    this.bookingPage.displayOptions(lid, false);
                    break;
                case "3":
                    commentsPage.displayListingCommentOption(lid);
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    public void displayEditOptions(int lid) {
        System.out.println("Editing options for listing " + lid);
        System.out.print("    1. Change price\n    2. Add amenities\n    3. Remove amenities\n    4. Add available date\n    5. Remove available date\n    6. Delete listing\nChoose your option: ");
        String editChoice = inputScanner.nextLine();
        switch (editChoice) {
            case "1" -> changePrice(lid);
            case "2" -> addAmenities(lid);
            case "3" -> removeAmenities(lid);
            case "4" -> addAvailability(lid);
            case "5" -> removeAvailability(lid);
            case "6" -> delete(lid);
            default -> System.out.println("Unknown edit choice.");
        }
    }

    void changePrice(int lid) {
        System.out.println("Enter new price: ");
        double newPrice = Double.parseDouble(inputScanner.nextLine());
        listingMethods.updatePrice(this.currentListing.host, lid, newPrice);
    }

    void addAmenities(int lid) {
        ArrayList<String> amenities = new ArrayList<>();
        boolean finishChoice = false;
        String[] supportedAmenities = {"Kitchen", "Internet", "TV", "Essentials", "Heating", "Air Conditioning", "Washer", "Dryer", "Free Parking", "Wireless", "Breakfast", "Pets", "Family Friendly", "Suitable for Events", "Smoking", "Wheelchair Accessible", "Elevator", "Fireplace", "Buzzer", "Doorman", "Pool", "Hot Tub", "Gym", "24 Hours Check-In", "Hangers", "Iron", "Hair Dryer", "Laptop-friendly Workspace", "Carbon Monoxide Detector", "First Aid Kit", "Smoke Detector"};
        while (!finishChoice) {
            System.out.println("Choose listings amenities:\n" +
                    "1. Kitchen            2. Internet                     3. TV                      4. Essentials        5. Heating\n" +
                    "6, Air Conditioning   7. Washer                       8. Dryer                   9. Free Parking      10. Wireless\n" +
                    "11. Breakfast         12. Pets                        13. Family Friendly        14. Suitable for Events\n" +
                    "15. Smoking           16. Wheelchair Accessible       17. Elevator               18. Fireplace        19. Buzzer\n" +
                    "20. Doorman           21. Pool                        22. Hot Tub                23. Gym              24. 24 Hours Check-In\n" +
                    "25. Hangers           26. Iron                        27. Hair Dryer             28. Laptop-friendly Workspace\n" +
                    "29. Carbon Monoxide Detector                          30. First Aid Kit          31.Smoke Detector\n");
            System.out.println("Chosen amenities: " + Arrays.toString(amenities.toArray()));
            System.out.print("Choose amenity to add, enter 0 to finish choosing: ");
            int choice = Integer.parseInt(inputScanner.nextLine());
            if (choice == 0) {
                finishChoice = true;
            } else {
                amenities.add(supportedAmenities[choice-1]);
            }
        }
        listingMethods.addAmenities(lid, amenities);
    }

    void removeAmenities(int lid) {
        ArrayList<String> amenities = new ArrayList<>();
        boolean finishChoice = false;
        while (!finishChoice) {
            System.out.println("Listing " + lid + "current amenities: ");
            for (int i = 1; i <= this.currentListing.amenities.size(); i++) {
                System.out.println("  " + i + ", " + this.currentListing.amenities.get(i-1));
            }
            System.out.println("Chosen amenities: " + Arrays.toString(amenities.toArray()));
            System.out.print("Choose amenity to add, enter 0 to finish choosing: ");
            int choice = Integer.parseInt(inputScanner.nextLine());
            if (choice == 0) {
                finishChoice = true;
            } else {
                amenities.add(this.currentListing.amenities.get(choice - 1));
            }
        }
        listingMethods.removeAmenities(lid, amenities);
    }

    void addAvailability(int lid) {
        System.out.println("Enter a date to add (YYYY-MM-DD): ");
        Date newDate = Date.valueOf(LocalDate.parse(inputScanner.nextLine()));
        listingMethods.addAvailability(lid, newDate);
    }

    void removeAvailability(int lid) {
        System.out.println("Enter a date to remove (YYYY-MM-DD): ");
        Date removeDate = Date.valueOf(LocalDate.parse(inputScanner.nextLine()));
        listingMethods.removeAvailability(lid, removeDate);
    }

    void delete(int lid) {
        System.out.println("Are you sure you want to delete this listing? (y/n)");
        String choice = inputScanner.nextLine();
        if (choice.equals("y") || choice.equals("Y")) {
            System.out.println("Removing listing...");
            listingMethods.removeListing(this.currentListing.host, lid);
        } else {
            System.out.println("Cancel...");
        }
    }

}
