package textUI;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import mybnb.*;
import dataObjects.*;

public class HostHome {
    public int hostId;
    public ListingPage listingPage;
    public CommentsPage commentsPage;
    public ListingMethods listingMethods;
    public AccountMethod accountMethod;
    public HostToolKit hostToolKit;
    public Scanner inputScanner;

    public HostHome(int hostId) {
        this.hostId = hostId;
        this.listingPage = new ListingPage(hostId);
        this.commentsPage = new CommentsPage();
        this.listingMethods = new ListingMethods();
        this.accountMethod = new AccountMethod();
        this.hostToolKit = new HostToolKit();
        this.inputScanner = new Scanner(System.in);
        System.out.println("\nLogged in as host: " + hostId);
    }

    public void showListings() {
        System.out.println("Listings from host:");
        ArrayList<Listing> listings = listingMethods.getHostListings(this.hostId);
        for (Listing l : listings) {
            System.out.println(l.toString());
        }
    }

    public void displayOptions() {
        boolean exit = false;
        while (!exit) {
            showListings();
            System.out.print("Home:\n    1. Make new listing\n    2. See Listing\n    3. Account details\n    4. View comments\n    5. Logout\nChoose an option: ");
            String userInput = inputScanner.nextLine();
            switch (userInput) {
                case "1":
                    makeListing();
                    break;
                case "2":
                    seeListing();
                    break;
                case "3":
                    accountDetails();
                    break;
                case "4":
                    commentsPage.displayUserOptions(hostId, true);
                    break;
                case "5":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
        System.out.println("Logging out...");
    }

    public void seeListing() {
        System.out.print("Input a listing id to view the listing: ");
        int lid = Integer.parseInt(inputScanner.nextLine());
        listingPage.displayOptions(lid);
    }

    public void accountDetails()  {
        String username = accountMethod.getUsernameFromId(this.hostId);
        ResultSet account = accountMethod.getAccountInfo(username);
        System.out.println("\nAccount info:");
        try {
            account.next();
            System.out.println("  User Type: Host");
            System.out.println("  User Id: " + this.hostId);
            System.out.println("  Authentication: " + account.getString("username") + ":" + account.getString("password"));
            System.out.println("  Name: " + account.getString("first_name") + " " + account.getString("last_name"));
            System.out.println("  Date of Birth: " + account.getDate("dob"));
            System.out.println("  Occupation: " + account.getString("occupation"));
            System.out.println("  SIN: " + account.getInt("sin"));
            System.out.println("  Address: " + account.getString("address"));

            System.out.print("\nGo back: [Enter]");
            inputScanner.nextLine();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeListing() {
        // Create a listing object
        System.out.print("Supported listing types:\n    1. Entire Place\n    2. Hotel Rooms\n    3. Private Rooms\n    4. Shared Rooms\n    5. Others\nChoose your listing type (default is 5): ");
        String type;
        String typeChoice = inputScanner.nextLine();
        switch (typeChoice) {
            case "1" -> type = "Entire Place";
            case "2" -> type = "Hotel Rooms";
            case "3" -> type = "Private Rooms";
            case "4" -> type = "Shared Rooms";
            default -> type = "Others";
        }

        System.out.print("Enter the listing title: ");
        String title = inputScanner.nextLine();
        System.out.println("Give a brief description:");
        String desc = inputScanner.nextLine();
        System.out.print("Enter the number of bedrooms: ");
        int bedrooms_num = Integer.parseInt(inputScanner.nextLine());
        System.out.print("Enter the number of bathrooms: ");
        int bathrooms_num = Integer.parseInt(inputScanner.nextLine());
        System.out.print("Enter the latitude: ");
        double latitude = Double.parseDouble(inputScanner.nextLine());
        System.out.print("Enter the longitude: ");
        double longitude = Double.parseDouble(inputScanner.nextLine());

        // Add address
        System.out.print("Enter street address: ");
        String street = inputScanner.nextLine();
        System.out.print("Enter city: ");
        String city = inputScanner.nextLine();
        System.out.print("Enter country: ");
        String country = inputScanner.nextLine();
        System.out.print("Enter postal code: ");
        String postal = inputScanner.nextLine();

        // Add amenities
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
        // Show suggested price here
        double suggestedPrice = hostToolKit.suggestPrice(type, bedrooms_num, bathrooms_num, amenities);
        System.out.println("\nCalculated suggested price :" + suggestedPrice);

        // Ask for price
        System.out.print("\nEnter the rent price per day: ");
        double price = Double.parseDouble(inputScanner.nextLine());

        // Add available dates
        System.out.println("Enter a date range of availability (you can modify this later).");
        System.out.print("Enter start date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(inputScanner.nextLine());
        System.out.print("Enter end date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(inputScanner.nextLine());

        // Call create listing
        Listing newListing = new Listing(this.hostId, type, title, desc, bedrooms_num, bathrooms_num, price, latitude, longitude);
        int lid = listingMethods.addListing(newListing);
        if (lid <= 0) { return; }

        Address newAddress = new Address(street, city, country, postal);
        boolean setAddress = listingMethods.setAddress(lid, newAddress);
        if (!setAddress) {return;}

        boolean addAmenities = listingMethods.addAmenities(lid, amenities);
        if (!addAmenities) { return; }

        for (LocalDate i = start; i.isBefore(end) || i.isEqual(end); i = i.plusDays(1)) {
            listingMethods.addAvailability(lid, Date.valueOf(i));
        }
        System.out.println("Listing " + lid + "is created.");
    }
}
