package com.idea.solutions.ideasoft;

public class ModelPage {
    //variables
    String authorName, content, id, published, selfLink, title, updated, url;

    //constructor
    public ModelPage(String authorName, String content, String id, String published, String selfLink, String title, String updated, String url) {
        this.authorName = authorName;
        this.content = content;
        this.id = id;
        this.published = published;
        this.selfLink = selfLink;
        this.title = title;
        this.updated = updated;
        this.url = url;
    }

    //Getter & Setter

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
