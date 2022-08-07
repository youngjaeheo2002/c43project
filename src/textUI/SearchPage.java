package textUI;

import mybnb.*;
import dataObjects.*;

import java.util.ArrayList;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class SearchPage {
    public Searches searches;
    public Scanner inputScanner;

    public SearchPage() {
        this.searches = new Searches();
        this.inputScanner = new Scanner(System.in);
    }

    public void displayOptions(int uid) {
        boolean exit = false;
        while (!exit) {
            System.out.print("\nSearch page:\n    1. Search within distance\n    2. Search by postal code\n    3. Search exact address\n    4. Return\nChoose search option: ");
            String userInput = inputScanner.nextLine();
            switch (userInput) {
                case "1":
                case "2":
                case "3":
                    int code = Integer.parseInt(userInput);
                    executeSearch(code, uid);
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown choice");
            }
        }
    }

    public void executeSearch(int code, int uid) {
        String opcode = "000";
        double longitude = 0, latitude = 0, distance = 0;
        String postal = null, country = null, city = null, street = null, distance_order = null;
        switch (code) {
            case 1:
                opcode = "100";
                System.out.print("Enter your current latitude: ");
                latitude = Double.parseDouble(inputScanner.nextLine());
                System.out.print("Enter your current longitude: ");
                longitude = Double.parseDouble(inputScanner.nextLine());
                System.out.print("Enter search radius in km: ");
                distance = Double.parseDouble(inputScanner.nextLine());
                System.out.print("Enter the distance ordering (asc/desc): ");
                distance_order = inputScanner.nextLine();
                break;
            case 2:
                opcode = "010";
                System.out.print("Enter the country you want to search: ");
                country = inputScanner.nextLine();
                System.out.print("Enter the postal code of the area you want to search: ");
                postal = inputScanner.nextLine();
                break;
            case 3 :
                opcode = "001";
                System.out.print("Enter the country you want to search: ");
                country = inputScanner.nextLine();
                System.out.print("Enter the city you want to search: ");
                city = inputScanner.nextLine();
                System.out.print("Enter the street address: ");
                street = inputScanner.nextLine();
        }
        // Propmt date rance
        System.out.println("Do you want to specify a date range of availability? (y/n)");
        String userDateChoice = inputScanner.nextLine();
        String start = null, end = null;
        if (userDateChoice.toLowerCase().equals("y")) {
            opcode += "1";
            System.out.print("  Enter the start date: ");
            start = inputScanner.nextLine();
            System.out.print("  Enter the end date: ");
            end = inputScanner.nextLine();
        } else {
            opcode += 0;
        }
        // Prompt price range
        System.out.println("Do you want to specify a price range? (y/n)");
        String userPriceChoice = inputScanner.nextLine();
        double lowest = 0, highest = 0;
        if (userPriceChoice.toLowerCase().equals("y")) {
            opcode += "1";
            System.out.print("  Enter the lowest price: ");
            lowest = Double.parseDouble(inputScanner.nextLine());
            System.out.print("  Enter the highest price: ");
            highest = Double.parseDouble(inputScanner.nextLine());
        } else {
            opcode += "0";
        }
        // Prompt amenities
        System.out.println("Are there amenities that you are looking for? (y/n)");
        String userAmenitiesChoice = inputScanner.nextLine();
        ArrayList<String> amenities = new ArrayList<>();
        if (userAmenitiesChoice.toLowerCase().equals("y")) {
            opcode += "1" ;
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
                    amenities.add(supportedAmenities[choice - 1]);
                }
            }
        } else {
            opcode += "0";
        }
        // call search
        ResultSet rs = searches.fullSearch(opcode, latitude, longitude, distance, distance_order, postal, country, city, street, start, end, lowest, highest, amenities);
        try {
            ArrayList<Listing> results = Listing.buildListingArray(rs);
            SearchResultPage resultPage = new SearchResultPage(results, uid, code == 1);
            resultPage.displayOptions();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve results");
        }
    }

}
