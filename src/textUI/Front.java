package textUI;

import java.util.Scanner;
import mybnb.*;

public class Front {
    public AccountMethod accountMethod;

    public Front() {
        this.accountMethod = new AccountMethod();
    }

    public void login() {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = inputScanner.nextLine();
        System.out.print("Enter your password: ");
        String passwrd = inputScanner.nextLine();

        try {
            // Call login
            boolean loginResult = accountMethod.Login(username, passwrd);
            if (!loginResult) {
                System.out.println("Login unsuccessful, please check your username and password.");
                return;
            }
            int uid = accountMethod.getIdFromUsername(username);
            if (uid == 0) { return; }
            boolean renter = accountMethod.isaRenter(uid);
            if (renter) {
                RenterHome renterHome = new RenterHome(uid);
                renterHome.displayOptions();
            } else {
                HostHome hostHome = new HostHome(uid);
                hostHome.displayOptions();
            }

        } catch (Exception e) {
            System.out.println("Error occurred during login.");
        }
    }

    public void register() {
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = inputScanner.nextLine();
        System.out.print("Enter your password: ");
        String passwrd = inputScanner.nextLine();
        System.out.print("Enter your password again: ");
        String passwrd2 = inputScanner.nextLine();
        if (!passwrd2.equals(passwrd)) {
            System.out.println("Passwords do not match.");
            return;
        }
        // Call register
        System.out.println("Enter your firstname: ");
        String first_name = inputScanner.nextLine();
        System.out.println("Enter your lastname: ");
        String last_name = inputScanner.nextLine();
        System.out.println("Enter your date of birth in the format YYYY-MM-DD: ");
        String dob = inputScanner.nextLine();
        System.out.println("Enter your address");
        String addr = inputScanner.nextLine();
        System.out.println("Enter your occupation: ");
        String occupation = inputScanner.nextLine();
        System.out.println("Enter your SIN: ");
        int sin = Integer.parseInt(inputScanner.nextLine());

        int registerResult = accountMethod.SignUp(username, passwrd, last_name, first_name, dob, addr, occupation, sin);
        // Check if register is successful
        if (registerResult != 0) {
            System.out.println("An error occurred, your username may have been taken.");
            return;
        }
        // check role of user
        System.out.println("\nAre you signing up as a [R]enter or a [H]ost? Default is Host.");
        String role = inputScanner.nextLine();
        if (role.equals("R") || role.equals("r")) {
            int uid = accountMethod.getIdFromUsername(username);

            System.out.print("Enter your credit card number: ");
            long card_num = Long.parseLong(inputScanner.nextLine());
            System.out.print("Supported card type:\n    1. Visa\n    2. Master Card\n    3. American Express\n    4. Discover\n Choose your card type: ");
            String card_type = null;
            String cardOption = inputScanner.nextLine();
            switch (cardOption) {
                case "1" -> card_type = "Visa";
                case "2" -> card_type = "Master Card";
                case "3" -> card_type = "American Express";
                case "4" -> card_type = "Discover";
                default -> card_type = "Other";
            }
            System.out.print("Enter card expire date in YYYY-MM: ");
            String expire_date = inputScanner.nextLine() + "-01";

            try {
                accountMethod.addCreditCard(card_num, card_type, expire_date, uid);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        System.out.println("Successfully registered a new account: " + username);
    }
}
