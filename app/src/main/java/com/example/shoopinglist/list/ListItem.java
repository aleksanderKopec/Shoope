package com.example.shoopinglist.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.shoopinglist.R;

public class ListItem implements Parcelable {
    public static final Creator<ListItem> CREATOR = new Creator<ListItem>() {
        @Override
        public ListItem createFromParcel(Parcel source) {
            return new ListItem(source);
        }

        @Override
        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };
    private String text;
    private int id;
    private int amount;
    private double value;

    public ListItem(String text, int id, int amount, double value) {
        this.text = text;
        this.id = id;
        this.amount = amount;
        this.value = value;
    }

    public ListItem() {
    }

    public ListItem(String text, int amount, double value) {
        this.text = text;
        this.amount = amount;
        this.value = value;
    }

    protected ListItem(Parcel in) {
        this.text = in.readString();
        this.id = in.readInt();
        this.amount = in.readInt();
        this.value = in.readDouble();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeInt(this.id);
        dest.writeInt(this.amount);
        dest.writeDouble(this.value);
    }

    public void readFromParcel(Parcel source) {
        this.text = source.readString();
        this.id = source.readInt();
        this.amount = source.readInt();
        this.value = source.readDouble();
    }
}
