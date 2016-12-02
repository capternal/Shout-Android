package com.shout.shoutin.main.Model;

/**
 * Created by jupitor on 26/10/16.
 */

public class SearchModel {

    private String id;
    private String title;

    public SearchModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
