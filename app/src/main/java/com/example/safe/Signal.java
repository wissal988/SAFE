package com.example.safe;

public class Signal {
    private String id;
    private String title;
    private String description;
    private String location;
    private String degree;
    private String type;
    private String userId;
    private int etat; // 0 = non traité, 1 = validé, 2 = refusé

    public Signal() {} // constructeur vide pour Firebase

    public Signal(String id, String title, String description, String location, String degree,
                  String type, String userId, int etat) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.degree = degree;
        this.type = type;
        this.userId = userId;
        this.etat = etat;
    }

    // getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }
}
