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

    public Item() {}

    public Item(int id, String title, String description, String category,
                String type, String location, String contactName,
                String contactPhone, String imageUri, String dateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.location = location;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.imageUri = imageUri;
        this.dateTime = dateTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}
