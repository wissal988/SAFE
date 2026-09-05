package com.example.safe;

public class Reclamation {
    private String reclamationId;
    private String title;
    private String description;
    private String location;
    private String userId;
    private int etat;
    private long timestamp;

    public Reclamation() {}

    public String getReclamationId() { return reclamationId; }
    public void setReclamationId(String id) { this.reclamationId = id; }

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
