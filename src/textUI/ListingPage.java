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
    public HostToolKit hostToolKit;
    public Scanner inputScanner;
    public Listing currentListing;
    public BookingPage bookingPage;

    public ListingPage(int hostId) {
        this.hostId = hostId;
        this.commentsPage = new CommentsPage();
        this.listingMethods = new ListingMethods();
        this.hostToolKit = new HostToolKit();
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
        System.out.println("\nEditing options for listing " + lid);
        System.out.print("    1. Change price\n    2. Show suggested amenities\n    3. Add amenities\n    4. Remove amenities\n    5. Add available date\n    6. Remove available date\n    7. Delete listing\nChoose your option: ");
        String editChoice = inputScanner.nextLine();
        switch (editChoice) {
            case "1" -> changePrice(lid);
            case "2" -> showSuggestedAmenities(lid);
            case "3" -> addAmenities(lid);
            case "4" -> removeAmenities(lid);
            case "5" -> addAvailability(lid);
            case "6" -> removeAvailability(lid);
            case "7" -> delete(lid);
            default -> System.out.println("Unknown edit choice.");
        }
    }

    void changePrice(int lid) {
        System.out.println("Enter new price: ");
        double newPrice = Double.parseDouble(inputScanner.nextLine());
        listingMethods.updatePrice(this.currentListing.host, lid, newPrice);
    }

    void showSuggestedAmenities(int lid) {
        ArrayList<String> suggested = new ArrayList<>();
        double potentialInc = hostToolKit.suggestAmenities(suggested, lid);
        System.out.println(" Suggested essential amenities: " + Arrays.toString(suggested.toArray()));
        System.out.println(" Potential price increase: +" + potentialInc);
        System.out.println("Return - [Enter]");
        inputScanner.nextLine();
    }

    void addAmenities(int lid) {
        ArrayList<String> amenities = new ArrayList<>();
        boolean finishChoice = false;
        String[] supportedAmenities = {"Kitchen", "Internet", "TV",
                "Toilet paper","Hand soap", "Shampoo","Body Wash or Bar Soap","One Towel Per Guest","One Pillow Per Guest","Sauna", "Linens for each guest bed",
                "Heating", "Air Conditioning", "Washer", "Dryer", "Free Parking", "Wireless", "Breakfast", "Pets", "Family Friendly", "Suitable for Events", "Smoking", "Wheelchair Accessible", "Elevator", "Fireplace", "Buzzer", "Doorman", "Pool", "Hot Tub", "Gym", "24 Hours Check-In", "Hangers", "Iron", "Hair Dryer", "Laptop-friendly Workspace", "Carbon Monoxide Detector", "First Aid Kit", "Smoke Detector"};
        while (!finishChoice) {
            System.out.println("Choose listings amenities:\n" +
                    "1. Kitchen            2. Internet                     3. TV                      4. Toiler paper\n" +
                    "5. Hand soap          6. Shampoo                      7. Body Wash or Bar Soap   8. One Towel Per Guess      9. One Pillow Per Guest\n" +
                    "10. Sauna             11. Lines for each guest bed    12. Heating                13, Air Conditioning        14. Washer\n" +
                    "15. Dryer             16. Free Parking                17. Wireless               18. Breakfast               19. Pets\n" +
                    "20. Family Friendly   21. Suitable for Events         22. Smoking                23. Wheelchair Accessible   24. Elevator\n" +
                    "25. Fireplace         26. Buzzer                      27. Doorman                28. Pool                    29. Hot Tub\n" +
                    "30. Gym               31. 24 Hours Check-In           32. Hangers                33. Iron                    34. Hair Dryer\n" +
                    "35. Laptop-friendly Workspace           36. Carbon Monoxide Detector             37. First Aid Kit           38.Smoke Detector\n");
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
