package textUI;

import mybnb.AccountMethod;

import java.awt.print.Book;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RenterHome {
    public int renterId;
    public BookingPage bookingPage;
    public AccountMethod accountMethod;
    public Scanner inputScanner;

    public RenterHome(int renterId) {
        this.renterId = renterId;
        this.bookingPage = new BookingPage();
        this.accountMethod = new AccountMethod();
        this.inputScanner = new Scanner(System.in);
        System.out.println("\nLogged in as renter: " + renterId);
    }

    public void displayOptions() {
        boolean exit = false;
        while (!exit) {
            System.out.print("Home:\n    1. Search listings\n    2. Your bookings\n    3. Account details\n    4. View comments\n    5.Logout\nChoose an option: ");
            String userInput = inputScanner.nextLine();
            switch (userInput) {
                case "1":

                    break;
                case "2":
                    this.bookingPage.displayOptions(renterId, true);
                    break;
                case "3":
                    accountDetails();
                    break;
                case "4":

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

    public void accountDetails()  {
        String username = accountMethod.getUsernameFromId(this.renterId);
        System.out.println("\nAccount info:");
        try {
            ResultSet account = accountMethod.getAccountInfo(username);
            ResultSet creditCards = accountMethod.getCreditCards(username);
            account.next();
            System.out.println("  User Type: Host");
            System.out.println("  User Id: " + this.renterId);
            System.out.println("  Authentication: " + account.getString("username") + ":" + account.getString("password"));
            System.out.println("  Name: " + account.getString("first_name") + " " + account.getString("last_name"));
            System.out.println("  Date of Birth: " + account.getDate("dob"));
            System.out.println("  Occupation: " + account.getString("occupation"));
            System.out.println("  SIN: " + account.getInt("sin"));
            System.out.println("  Address: " + account.getString("address"));

            System.out.println("\nCredit cards:");
            while (creditCards.next()) {
                System.out.println("  Card: " + creditCards.getInt("cardID"));
                System.out.println("  Card number: " + creditCards.getInt("card_num"));
                System.out.println("  Expire date: " + creditCards.getDate("expiry_date"));
                System.out.println("  Card type: " + creditCards.getString("card_type"));
                System.out.println("");
            }

            System.out.print("Go back: [Enter]");
            inputScanner.nextLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
