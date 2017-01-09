package com.shoutin.main.Model;

import com.shoutin.login.model.ContactModel;

import java.util.ArrayList;

/**
 * Created by CapternalSystems on 6/8/2016.
 */
public class Continent {

    private String Header;
    private ArrayList<ContactModel> arrContactList = new ArrayList<ContactModel>();
    private boolean visible;

    public Continent(String header, ArrayList<ContactModel> arrContactList, boolean visible) {
        Header = header;
        this.arrContactList = arrContactList;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getHeader() {
        return Header;
    }

    public void setHeader(String header) {
        Header = header;
    }

    public ArrayList<ContactModel> getArrContactList() {
        return arrContactList;
    }

    public void setArrContactList(ArrayList<ContactModel> arrContactList) {
        this.arrContactList = arrContactList;
    }
}
