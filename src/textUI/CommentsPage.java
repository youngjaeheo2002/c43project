package textUI;

import mybnb.*;

import java.util.Map;
import java.util.Scanner;
import java.sql.*;

public class CommentsPage {

    public AccountMethod accountMethod;
    public CommentMethods commentMethods;
    public Scanner inputScanner;

    public CommentsPage() {
        this.accountMethod = new AccountMethod();
        this.commentMethods = new CommentMethods();
        this.inputScanner = new Scanner(System.in);
    }

    public void displayUserOptions(int uid, boolean isHost) {
        System.out.print("View:\n    1. Comment to you\n    2. Comments from you\nChoose an option: ");
        String userInput = inputScanner.nextLine();
            if (userInput.equals("1")) {
                displayCommentsToUser(uid);
            } else if (userInput.equals("2")) {
                displayCommentsFromUser(uid, isHost);
            } else {
                System.out.println("Unknown choice");
            }
    }

    public void displayCommentsFromUser(int uid, boolean isHost) {
        boolean exit = false;
        System.out.println("\nYour comments:");
        try {
            while (!exit) {
                ResultSet comments = accountMethod.getCommentsFrom(uid);
                while (comments.next()) {
                    System.out.println("Comment Id: " + comments.getInt("commentID"));
                    if (isHost) {
                        System.out.println("To Renter: " + comments.getInt("receiver"));
                    }
                    System.out.println("On Listing: " + comments.getInt("on_listing"));
                    System.out.println("Rating: " + comments.getInt("rating"));
                    System.out.println("  " + comments.getString("comment"));
                    System.out.println("");
                }

                System.out.print("Comment option:\n     1. Delete comment\n     2. Return\nChoose an option: ");
                String userInput = inputScanner.nextLine();
                if (userInput.equals("1")) {
                    deleteComment();
                } else if(userInput.equals("2")) {
                    exit = true;
                } else{
                    System.out.println("Unknown choice");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteComment() {
        System.out.println("Enter the comment id you want to delete: ");
        int commentID = Integer.parseInt(inputScanner.nextLine());
        System.out.println("Are you sure you want to delete this comment? (y/n)");
        String userChoice = inputScanner.nextLine();
        if (userChoice.toLowerCase().equals("y")) {
            System.out.println("Deleting comment " + commentID + "...");
            accountMethod.deleteComment(commentID);
        } else {
            System.out.println("Cancelling...");
        }
    }

    public void displayCommentsToUser(int uid) {
        System.out.println("\nAverage rating: " + accountMethod.getUserAvgRating(uid));
        ResultSet comments = accountMethod.getCommentsAbout(uid);
        try {
            while (comments.next()) {
                System.out.println("Sender: " + comments.getInt("sender"));
                System.out.println("On Listing: " + comments.getInt("on_listing"));
                System.out.println("Rating: " + comments.getInt("rating"));
                System.out.println("  " + comments.getString("comment"));
                System.out.println("");
            }
            System.out.print("Return - [Enter]");
            inputScanner.nextLine();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayListingComments(int lid) {
        System.out.println("\nAverage rating: " + accountMethod.getListingAvgRating(lid));
        ResultSet comments = accountMethod.getCommentsAboutListing(lid);
        try {
            while (comments.next()) {
                System.out.println("Sender: " + comments.getInt("sender"));
                System.out.println("Rating: " + comments.getInt("rating"));
                System.out.println("  " + comments.getString("comment"));
                System.out.println("");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayListingTopNounPhrase(int lid) {
        Map<String, Integer> np_freq = commentMethods.analyseDocument(commentMethods.generateDocument(lid));
        System.out.println("Common noun phrases and their frequency:");
        int count = 0;
        for (Map.Entry<String, Integer> entry : np_freq.entrySet()) {
            System.out.println("  " + entry.getKey() + " - " + entry.getValue());
            count++;
            if (count >= 5){
                break;
            }
        }
        System.out.print("Return - [Enter]");
        inputScanner.nextLine();
    }

    public void displayListingCommentOption(int lid) {
        boolean exit = false;
        while (!exit) {
            displayListingComments(lid);
            System.out.print("Listing comment options:\n    1. Analyse listing comments\n    2. Return\nChoose an option: ");
            String userChoice = inputScanner.nextLine();
            if (userChoice.equals("1")) {
                displayListingTopNounPhrase(lid);
            } else if (userChoice.equals("2")) {
                exit = true;
            } else {
                System.out.println("Unknown option");
            }
        }
    }
}
