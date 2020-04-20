package com.example.springbreakprototype2;


import java.util.HashMap;


public class Product implements Comparable{
    private Double price;
    private String seller, title, description, category;
    private Object timestamp;
    private static String sortMethod = "Recent";
    private String id = "";
    private String goodService;
    private String [] postingImages;

    // product images
    private String image0;
    private String image1;
    private String image2;

    // for creating from the app
    public Product (Double price, String seller, String title, String description, Object time, String category, String image0, String image1, String image2) {
        this.price = price;
        this.seller = seller;
        this.title = title;
        this.description = description;
        this.timestamp = time;
        this.category = category;

        // images
        this.image0 = image0;
        this.image1 = image1;
        this.image2 = image2;
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

    public Object getTime() {
        return timestamp;
    }
    public String getGoodService() {
        return this.goodService;
    }
    public String getId() {
        return this.id;
    }

    public String getImage0() {
        return this.image0;
    }

    public String getImage1() {
        return this.image1;
    }

    public String getImage2() {
        return this.image2;
    }

    public String getThumbnailImage() {
        return this.getImage0();
    }

    // setter methods
    public void setTime(String s) {
        timestamp = s;
    }

    public void setGoodService(String s) {
        this.goodService = s;
    }

    public void setId(String s) {
        this.id = s;
    }

    @Override
    public int compareTo(Object compareProductTo) {
        if (sortMethod.equals("Recent")) {
            Object time = ((Product) compareProductTo).getTime();
            String s1;
            //uhh so problem with timestamp maybe being null when first created? idk if thats the reason hopefully this fixes it
            //i dont rly know what to do with it lol someone smarter than me pls fix it
            if (timestamp == null || timestamp.toString().equals("")) {
                s1 = "=" + Long.MAX_VALUE + ",";
            } else {
                s1 = timestamp.toString();
            }

            String s2;
            if (time == null || time.toString().equals("")) {
                s2 = "=" + Long.MAX_VALUE + ",";
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
