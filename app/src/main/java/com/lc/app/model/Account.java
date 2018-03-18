package com.lc.app.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.lc.app.BR;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public class Account extends BaseObservable implements Parcelable {

    private String name;

    private float remain;

    private String number;

    public Account() {
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
        notifyPropertyChanged(BR.remain);
    }

    @Bindable
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        notifyPropertyChanged(BR.number);
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", remain=" + remain +
                ", number='" + number + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeFloat(this.remain);
        dest.writeString(this.number);
    }

    protected Account(Parcel in) {
        this.name = in.readString();
        this.remain = in.readFloat();
        this.number = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
