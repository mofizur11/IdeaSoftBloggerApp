package com.idea.solutions.ideasoft;

public class ModelComment {

    //variable
    String id, name, profileImage, published, Comment;

    //constructor
    public ModelComment(String id, String name, String profileImage, String published, String Comment) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
        this.published = published;
        this.Comment = Comment;
    }

    //Getter and Setter

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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }
}
