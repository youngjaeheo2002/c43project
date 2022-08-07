package textUI;

import mybnb.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean exit = false;
        Front page = new Front();
        ReportPage report = new ReportPage();
        Scanner inputScanner = new Scanner(System.in);
        while (!exit) {
            System.out.print("\nMain menu:\n    1. Login\n    2. Register\n    3. Platform-wide reports\n    4. Exit\nChoose an option: ");
            String userInput = inputScanner.nextLine();
            switch (userInput) {
                case "1":
                    page.login();
                    break;
                case "2":
                    page.register();
                    break;
                case "3":
                    report.displayOptions();
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
        System.out.println("Exit.");
    }
}
