package com.android.monamie.models;

public class TeamMember {
    private String name;
    private String location;
    private String description;
    private int imageRes;
    private String stats; // e.g. "Programmer"
    private boolean isFollowing = false;
    private String instagramUrl;


    public TeamMember(String name, String location, String description, int imageRes, String stats, String instagramUrl) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageRes = imageRes;
        this.stats = stats;
        this.instagramUrl = instagramUrl;

    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public int getImageRes() { return imageRes; }
    public String getStats() { return stats; }
    public String getInstagramUrl() { return instagramUrl; }


    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}