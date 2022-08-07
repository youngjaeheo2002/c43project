package textUI;

import mybnb.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class ReportPage {
    public ReportMethods reportMethods;
    public Scanner inputScanner;
    public ReportPage() {
        this.reportMethods = new ReportMethods();
        this.inputScanner = new Scanner(System.in);
    }

    public void displayOptions() {
        boolean exit = false;
        while (!exit) {
            System.out.print("Report options:\n" +
                    "    1. Total bookings per city\n" +
                    "    2. Total bookings per postal\n" +
                    "    3. Total listings per country\n" +
                    "    4. Total listings per city\n" +
                    "    5. Total listings per postal code\n" +
                    "    6. Rank hosts by total listings per country\n" +
                    "    7. Rank hosts by total listings per city\n" +
                    "    8. Potential commercial host\n" +
                    "    9. Rank renters by total bookings\n" +
                    "    10. Rank renters by total bookings per city\n" +
                    "    11. Rank Users by cancellations\n" +
                    "Choose a report, choose 0 to return: ");
            String reportChoice = inputScanner.nextLine();
            Date start = null, end = null;
            switch (reportChoice) {
                case "0":
                    exit = true;
                    break;
                case "1", "2", "9", "10":
                    System.out.println("You must enter a date range for the report:");
                    System.out.print("  Enter the start date (YYYY-MM-DD): ");
                    start = Date.valueOf(LocalDate.parse(inputScanner.nextLine()));
                    System.out.print("  Enter the end date (YYYY-MM-DD): ");
                    end = Date.valueOf(LocalDate.parse(inputScanner.nextLine()));
                case "3", "4", "5", "6", "7", "8", "11":
                    invokeReport(Integer.parseInt(reportChoice), start, end);
                    break;
                default:
                    System.out.println("Unknown choice");
            }

        }
    }

    public void invokeReport(int reportCode, Date start, Date end) {
        String reportStr = "";
        switch (reportCode) {
            case 1 -> reportStr = "bookings_city";
            case 2 -> reportStr = "bookings_postal";
            case 3 -> reportStr = "listings_country";
            case 4 -> reportStr = "listings_city";
            case 5 -> reportStr = "listings_postal";
            case 6 -> reportStr = "host_country";
            case 7 -> reportStr = "host_city";
            case 8 -> reportStr = "commercial_host";
            case 9 -> reportStr = "renter_ranking";
        }
        ResultSet report = null;
        switch (reportCode) {
            case 1, 2, 9 -> report = reportMethods.reportDateRange(start, end, reportStr);
            case 3, 4, 5, 6, 7, 8 -> report = reportMethods.reportNoDate(reportStr);
            case 10 -> report = reportMethods.getRenterBookingsPerCity(start, end);
            case 11 -> report = reportMethods.getUserCancellation();
        }
        displayReportResult(report);
    }

    public void displayReportResult(ResultSet rs) {
        System.out.println("\n Report:");
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsCount = rsmd.getColumnCount();
            int rows = 0;
            boolean is_empty = true;

            while (rs.next()) {
                is_empty = false;
                StringBuilder s = new StringBuilder();
                if (rows == 0) {
                    s.append("| ");
                    for (int i = 1; i <= columnsCount; i++) {
                        s.append(rsmd.getColumnName(i));
                        s.append(" | ");
                    }
                    s.append("\n");
                }
                rows += 1;
                s.append("| ");
                for (int i = 1; i <= columnsCount; i++) {
                    if (i > 1) {
                        s.append(" | ");
                    }
                    s.append(rs.getObject(i));
                }
                s.append(" |");
                System.out.println(s);
            }

            if (is_empty) {
                System.out.println("Empty report");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred when displaying the report.");
        }
    }

}
