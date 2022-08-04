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
    public ListingMethods listingMethods;
    public AccountMethod accountMethod;
    public Scanner inputScanner;

    public HostHome(int hostId) {
        this.hostId = hostId;
        this.listingPage = new ListingPage();
        this.listingMethods = new ListingMethods();
        this.accountMethod = new AccountMethod();
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
            System.out.print("Home:\n    1. Make new listing\n    2. See Listing\n    3. Account details\n    4. Logout\nChoose an option: ");
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
        listingPage.displayOptions(lid, this.hostId);
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
        System.out.print("Enter the rent price per day: ");
        double price = Double.parseDouble(inputScanner.nextLine());
        System.out.print("Enter the latitude: ");
        double latitude = Double.parseDouble(inputScanner.nextLine());
        System.out.print("Enter the longitude: ");
        double longitude = Double.parseDouble(inputScanner.nextLine());
        // Call create listing
        Listing newListing = new Listing(this.hostId, type, title, desc, bedrooms_num, bathrooms_num, price, latitude, longitude);
        int lid = listingMethods.addListing(newListing);
        if (lid <= 0) { return; }
        // Add address
        System.out.print("Enter street address: ");
        String street = inputScanner.nextLine();
        System.out.print("Enter city: ");
        String city = inputScanner.nextLine();
        System.out.print("Enter country: ");
        String country = inputScanner.nextLine();
        System.out.print("Enter postal code: ");
        String postal = inputScanner.nextLine();
        Address newAddress = new Address(street, city, country, postal);
        boolean setAddress = listingMethods.setAddress(lid, newAddress);
        if (!setAddress) {return;}
        // Add amenities
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
        boolean addAmenities = listingMethods.addAmenities(lid, amenities);
        if (!addAmenities) { return; }
        // Add available dates
        System.out.println("Enter a date range of availability (you can modify this later).");
        System.out.print("Enter start date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(inputScanner.nextLine());
        System.out.print("Enter end date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(inputScanner.nextLine());
        for (LocalDate i = start; i.isBefore(end) || i.isEqual(end); i = i.plusDays(1)) {
            listingMethods.addAvailability(lid, Date.valueOf(i));
        }
        System.out.println("Listing " + lid + "is created.");
        return;
    }
}
