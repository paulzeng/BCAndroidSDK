package com.beintoo.wrappers;

public class ShareContactWrapper {
    private String id;
    private String name;
    private String contact;
    private String imageUrl;
    private boolean selected = false;


    public ShareContactWrapper(String id, String name, String contact, String imageUrl) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.imageUrl = imageUrl;
    }

    public ShareContactWrapper() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * toString is used in the search filter of the listview
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
