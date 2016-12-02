package com.shout.shoutin.main.Model;

import java.util.ArrayList;

/**
 * Created by jupitor on 26/10/16.
 */

public class SearchContinent {

    private String Header;
    private ArrayList<SearchModel> arrSearchModelData = new ArrayList<SearchModel>();

    public SearchContinent(String header, ArrayList<SearchModel> countryList) {
        Header = header;
        this.arrSearchModelData = countryList;
    }

    public String getHeader() {
        return Header;
    }

    public void setHeader(String header) {
        Header = header;
    }

    public ArrayList<SearchModel> getArrSearchModelData() {
        return arrSearchModelData;
    }

    public void setArrSearchModelData(ArrayList<SearchModel> arrSearchModelData) {
        this.arrSearchModelData = arrSearchModelData;
    }

}
