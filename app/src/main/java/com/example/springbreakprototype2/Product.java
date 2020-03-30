package com.example.springbreakprototype2;


import java.util.HashMap;


public class Product implements Comparable{
    private Double price;
    private String seller, title, description, category;
    private Object timestamp;
    private static String sortMethod = "Recent";

    public Product (Double price, String seller, String title, String description, Object time, String category) {
        this.price = price;
        this.seller = seller;
        this.title = title;
        this.description = description;
        this.timestamp = time;
        this.category = category;
    }

    public HashMap<String, String> getData() {
        HashMap<String, String> arr = new HashMap<String, String>();
        arr.put("price", price.toString());
        arr.put("seller", seller);
        arr.put("title", title);
        arr.put("description", description);
        return arr;
    }

    public static void setSortMethod(String s) {
        sortMethod = s;
    }

    public static String getSortMethod() {
        return sortMethod;
    }

    public Double getPrice() {
        return price;
    }
    public String getSeller() {
        return seller;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getCategory() { return category; }

    public void setTime(String s) {
        timestamp = s;
    }

    public Object getTime() {
        return timestamp;
    }

    //CHANGE THIS
    public int getImgId() {
        return android.R.drawable.ic_delete;
    }

    @Override
    public int compareTo(Object compareProductTo) {
        if (sortMethod.equals("Recent")) {
            Object time = ((Product) compareProductTo).getTime();
            String s1;
            //uhh so problem with timestamp maybe being null when first created? idk if thats the reason hopefully this fixes it
            //i dont rly know what to do with it lol someone smarter than me pls fix it
            if (timestamp == null || timestamp == "") {
                s1 = "," + Double.MAX_VALUE + ",";
            } else {
                s1 = timestamp.toString();
            }

            String s2;
            if (time == null || time == "") {
                s2 = "," + Double.MAX_VALUE + ",";
            } else {
                s2 = time.toString();
            }

            long t1 = Long.parseLong(s1.substring(s1.indexOf("=") + 1, s1.indexOf(",")));
            long t2 = Long.parseLong(s2.substring(s2.indexOf("=") + 1, s2.indexOf(",")));
            return (int) (t2-t1);
        } else if (sortMethod.equals("Price descending")) {
            return (int) ((((Product) compareProductTo).getPrice() - price) * 100);
        } else {
            return (int) ((price - ((Product) compareProductTo).getPrice()) * 100);
        }

    }

}
