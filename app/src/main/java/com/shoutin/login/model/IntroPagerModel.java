package com.shoutin.login.model;

/**
 * Created by jupitor on 14/12/16.
 */

public class IntroPagerModel {

    private int imageId;
    private String text;

    public IntroPagerModel(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
     }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
