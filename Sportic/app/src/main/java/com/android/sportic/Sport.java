package com.android.sportic;

public class Sport {

    //Fields
    private String name;
    private String tag;

    //Constructor
    public Sport(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    //Methods
    public String getName() { return name; }
    public String getTag() { return tag; }
    public void setName(String name) { this.name = name; }

}
