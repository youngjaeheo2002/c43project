package mybnb;


import dataObjects.Listing;

import java.util.ArrayList;
import java.util.ListIterator;

public class HostToolKit extends Methods {
    ListingMethods l = new ListingMethods();

    public double suggestPrice(String type, int num_bedrooms, int num_bathrooms, ArrayList<String> amenities){
        double price = 0;
        if (type.equals("Entire Place")){
            price += 100;
        }

        else if (type.equals("Hotel Rooms")){
            price += 75;
        }

        else if (type.equals("Private Rooms")){
            price +=  60;
        }

        else if (type.equals("Shared Rooms")){
            price += 30;
        }

        else if (type.equals("Other")){
            System.out.println("Cannot suggest price for 'other'");
            return -1;
        } else {
            System.out.println("Unknown type");
            return -1;
        }

        price += num_bedrooms * 50;
        price += num_bathrooms * 15;
        ListIterator<String> a = amenities.listIterator();
        while(a.hasNext()){
            String next = a.next();
            if (next.equals("Pool") || next.equals("Wireless") || next.equals("Kitchen")
                    || next.equals("Free Parking") || next.equals("Hot Tub") || next.equals("Washer")
                    || next.equals("Dryer") || next.equals("Air Conditioning") || next.equals("Heating")
                    || next.equals("Pets") || next.equals("24 Hours Check-In") || next.equals("Laptop-friendly Workspace")
                    || next.equals("24 Hours Check-In") || next.equals("Gym") || next.equals("Sauna")
                    || next.equals("Fireplace")
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

    public double suggestAmenities(ArrayList<String> suggested, int lid) {
        Listing listing = l.getListingById(lid);

        ArrayList<String> essentials = new ArrayList<String>();
        essentials.add("Toilet paper");
        essentials.add("Hand soap");
        essentials.add("Shampoo");
        essentials.add("Body Wash or Bar Soap");
        essentials.add("One Towel Per Guest");
        essentials.add("One Pillow Per Guest");
        essentials.add("Sauna");
        essentials.add("Linens for each guest bed");
        ListIterator<String> i = essentials.listIterator();
        ArrayList<String> amenities = listing.amenities;
        while(i.hasNext()){
            String next = i.next();
            if (! amenities.contains(next)){
                suggested.add(next);
            }
        }
        return suggested.size()*12;
    }



}
