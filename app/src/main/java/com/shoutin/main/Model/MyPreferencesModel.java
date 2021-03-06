package com.shoutin.main.Model;

/**
 * Created by CapternalSystems on 7/19/2016.
 */
public class MyPreferencesModel {

    private String id;
    private String preference_id;
    private String title;
    private String status;
    private boolean IsCheckBoxChecked;
    private String hideCheckBox;
    private String image;
    private String message;


    public MyPreferencesModel(String id, String preference_id, String title, String status, boolean isCheckBoxChecked, String hideCheckBox, String image, String message) {
        this.id = id;
        this.preference_id = preference_id;
        this.title = title;
        this.status = status;
        IsCheckBoxChecked = isCheckBoxChecked;
        this.hideCheckBox = hideCheckBox;
        this.image = image;
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreference_id() {
        return preference_id;
    }

    public void setPreference_id(String preference_id) {
        this.preference_id = preference_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCheckBoxChecked() {
        return IsCheckBoxChecked;
    }

    public void setCheckBoxChecked(boolean checkBoxChecked) {
        IsCheckBoxChecked = checkBoxChecked;
    }

    public String getHideCheckBox() {
        return hideCheckBox;
    }

    public void setHideCheckBox(String hideCheckBox) {
        this.hideCheckBox = hideCheckBox;
    }
}
