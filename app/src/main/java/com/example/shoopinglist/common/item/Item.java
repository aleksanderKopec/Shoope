package com.example.shoopinglist.common.item;

import com.example.shoopinglist.R;

public abstract class Item {

    private String text;

    static int getLayout() {
        return R.layout.common_item;
    }
    abstract String getText();
    abstract String setText();
}
