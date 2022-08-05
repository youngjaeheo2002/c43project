package textUI;

import mybnb.*;
import dataObjects.*;

import java.util.ArrayList;

public class SearchPage {

    public SearchPage() {

    }

    public void displayOptions(int uid) {
        System.out.println("\nSearch page: ");
        ArrayList<Listing> results = new ArrayList<>();
        SearchResultPage resultPage = new SearchResultPage(results, uid);
        resultPage.displayOptions();
    }

}
