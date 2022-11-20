package com.example.shoopinglist.list;

import com.example.shoopinglist.R;

public class ListItem {
    private final String text;
    private int id;

    public ListItem(String text) {
        this.text = text;
    }

    static int getLayout() {
        return R.layout.common_item;
    }

    public String getText() {
        return text;
    }

    public String setText() {
        return null;
    }
}
