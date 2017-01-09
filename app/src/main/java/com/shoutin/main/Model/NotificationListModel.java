package com.shoutin.main.Model;

/**
 * Created by CapternalSystems on 9/26/2016.
 */
public class NotificationListModel {

    private String id;
    private String message;
    private String user_id;
    private String username;
    private String notification_type;
    private String created;
    private String photo;
    private String is_read;
    private String shout_id;


    public NotificationListModel(String id, String message, String user_id, String username, String notification_type, String created, String photo, String is_read, String shout_id) {
        this.id = id;
        this.message = message;
        this.user_id = user_id;
        this.username = username;
        this.notification_type = notification_type;
        this.created = created;
        this.photo = photo;
        this.is_read = is_read;
        this.shout_id = shout_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getShout_id() {
        return shout_id;
    }

    public void setShout_id(String shout_id) {
        this.shout_id = shout_id;
    }
}
