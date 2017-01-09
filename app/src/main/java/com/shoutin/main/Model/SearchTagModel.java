package com.shoutin.main.Model;

/**
 * Created by jupitor on 08/11/16.
 */

public class SearchTagModel {

    private int searchedTagId;
    private String searchedTagWord;

    public SearchTagModel(int searchedTagId, String searchedTagWord) {
        this.searchedTagId = searchedTagId;
        this.searchedTagWord = searchedTagWord;
    }

    public int getSearchedTagId() {
        return searchedTagId;
    }

    public void setSearchedTagId(int searchedTagId) {
        this.searchedTagId = searchedTagId;
    }

    public String getSearchedTagWord() {
        return searchedTagWord;
    }

    public void setSearchedTagWord(String searchedTagWord) {
        this.searchedTagWord = searchedTagWord;
    }
}
