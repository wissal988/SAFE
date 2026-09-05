package com.example.safe;

public class Amelioration {
    private String ameliorationId;
    private String title;
    private String description;
    private String location;
    private String userId;
    private int etat;
    private long timestamp;

    public Amelioration() {}

    public String getAmeliorationId() { return ameliorationId; }
    public void setAmeliorationId(String id) { this.ameliorationId = id; }

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public String getLocation() { return location; }
    public void setLocation(String loc) { this.location = loc; }

    public String getUserId() { return userId; }
    public void setUserId(String id) { this.userId = id; }

    public int getEtat() { return etat; }
    public void setEtat(int e) { this.etat = e; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long t) { this.timestamp = t; }
}
