package com.example.shoopinglist.list;

import com.example.shoopinglist.R;

public class ListItem {
    private String text;
    private int id;
    private int amount;
    private double value;

    public ListItem(String text) {
        this.text = text;
    }

    static int getLayout() {
        return R.layout.common_item;
    }

    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
