package com.shoutin.login;

/**
 * Created by jupitor on 16/12/16.
 */

public class CustomListModel {
    private int id;
    private String name;

    public CustomListModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

