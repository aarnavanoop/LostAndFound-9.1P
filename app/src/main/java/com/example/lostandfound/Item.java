package com.example.lostandfound;

public class Item {
    private int id;
    private String title;
    private String description;
    private String category;
    private String type;
    private String location;
    private String contactName;
    private String contactPhone;
    private String imageUri;
    private String dateTime;
    private double latitude;
    private double longitude;

    public Item() {}


    public Item(int id, String title, String description, String category,
                String type, String location, String contactName,
                String contactPhone, String imageUri, String dateTime) {
        this(id, title, description, category, type, location,
             contactName, contactPhone, imageUri, dateTime, 0.0, 0.0);
    }


    public Item(int id, String title, String description, String category,
                String type, String location, String contactName,
                String contactPhone, String imageUri, String dateTime,
                double latitude, double longitude) {
        this.id           = id;
        this.title        = title;
        this.description  = description;
        this.category     = category;
        this.type         = type;
        this.location     = location;
        this.contactName  = contactName;
        this.contactPhone = contactPhone;
        this.imageUri     = imageUri;
        this.dateTime     = dateTime;
        this.latitude     = latitude;
        this.longitude    = longitude;
    }


    public int    getId()       { return id; }
    public void   setId(int id) { this.id = id; }

    public String getTitle()            { return title; }
    public void   setTitle(String v)    { this.title = v; }

    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description = v; }

    public String getCategory()         { return category; }
    public void   setCategory(String v) { this.category = v; }

    public String getType()         { return type; }
    public void   setType(String v) { this.type = v; }

    public String getLocation()         { return location; }
    public void   setLocation(String v) { this.location = v; }

    public String getContactName()         { return contactName; }
    public void   setContactName(String v) { this.contactName = v; }

    public String getContactPhone()         { return contactPhone; }
    public void   setContactPhone(String v) { this.contactPhone = v; }

    public String getImageUri()         { return imageUri; }
    public void   setImageUri(String v) { this.imageUri = v; }

    public String getDateTime()         { return dateTime; }
    public void   setDateTime(String v) { this.dateTime = v; }

    public double getLatitude()          { return latitude; }
    public void   setLatitude(double v)  { this.latitude = v; }

    public double getLongitude()         { return longitude; }
    public void   setLongitude(double v) { this.longitude = v; }

    /** Returns true only when real coordinates have been stored. */
    public boolean hasLocation() { return latitude != 0.0 && longitude != 0.0; }
}
