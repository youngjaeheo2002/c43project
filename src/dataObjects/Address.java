package dataObjects;

public class Address {
    public int aid;
    public String country;
    public String city;
    public String street;
    public String postal;

    public Address(String street, String city, String country, String postal) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.postal = postal;
    }
}
