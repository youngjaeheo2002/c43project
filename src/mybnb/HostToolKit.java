package mybnb;


import dataObjects.Listing;

import java.util.ArrayList;
import java.util.ListIterator;

public class HostToolKit extends Methods {
    ListingMethods l = new ListingMethods();

    public double suggestPrice(int lid){
        Listing listing = l.getListingById(lid);
        if (listing == null) {
            return -1;
        }
        double price = 0;
        if (listing.type.equals("Entire Place")){
            price += 100;
        }

        else if (listing.type.equals("Hotel Rooms")){
            price += 75;
        }

        else if (listing.type.equals("Private Rooms")){
            price +=  60;
        }

        else if (listing.type.equals("Shared Rooms")){
            price += 30;
        }

        else if (listing.type.equals("Other")){
            System.out.println("Cannot suggest price for 'other'");
            return -1;
        }

        price += listing.num_bathrooms * 50;
        price += listing.num_bathrooms * 15;
        ListIterator<String> a = listing.amenities.listIterator();
        while(a.hasNext()){
            String next = a.next();
            if (next.equals("Pool") || next.equals("Wireless") || next.equals("Kitchen")
                    || next.equals("Free Parking") || next.equals("Hot Tub") || next.equals("Washer")
                    || next.equals("Dryer") || next.equals("Air Conditioning") || next.equals("Heating")
                    || next.equals("Pets") || next.equals("24 Hours Check-In") || next.equals("Laptop-friendly Workspace")
                    || next.equals("24 Hours Check-In")
            ){
                price += 50;
            }
            else if (next.equals("Suitable for Events")){
                price += 300;
            }

            else{
                price += 15;
            }
        }

        return price;



    }

}
