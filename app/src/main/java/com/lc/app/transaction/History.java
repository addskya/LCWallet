package com.lc.app.transaction;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Orange on 18-3-29.
 * Email:addskya@163.com
 */

public class History extends BaseObservable implements Parcelable {


    /**
     * from : 0x4844a7e094f209285d81e32c84dfe6646ac1e45b
     * to : 0xc6382f2cee0b165862e465731ad8072491efe804
     * value : 10.00000000
     * time : Thu Mar 29 2018 22:44:32 GMT+0800 (CST)
     */

    private String from;
    private String to;
    private Float value;
    private String time;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeValue(this.value);
        dest.writeString(this.time);
    }

    public History() {
    }

    protected History(Parcel in) {
        this.from = in.readString();
        this.to = in.readString();
        this.value = (Float) in.readValue(Float.class.getClassLoader());
        this.time = in.readString();
    }

    public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {
        @Override
        public History createFromParcel(Parcel source) {
            return new History(source);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
