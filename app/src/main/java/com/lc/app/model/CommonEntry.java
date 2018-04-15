package com.lc.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public class CommonEntry implements Serializable, Parcelable {

    public static final int serialVersionUID = 0x00000001;

    private String mUUID;

    private String mName;

    private String mAddress;


    public CommonEntry() {
        this(null, null);
    }

    public CommonEntry(@Nullable String name,
                       @Nullable String address) {
        mName = name;
        mAddress = address;
        mUUID = UUID.randomUUID().toString();
    }

    public CommonEntry(@Nullable String name,
                       @Nullable String address,
                       @Nullable String uuid) {
        mName = name;
        mAddress = address;
        mUUID = uuid;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setName(@Nullable String name) {
        mName = name;
    }

    public void setAddress(@Nullable String address) {
        mAddress = address;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUUID);
        dest.writeString(this.mName);
        dest.writeString(this.mAddress);
    }

    protected CommonEntry(Parcel in) {
        this.mUUID = in.readString();
        this.mName = in.readString();
        this.mAddress = in.readString();
    }

    public static final Parcelable.Creator<CommonEntry> CREATOR = new Parcelable.Creator<CommonEntry>() {
        @Override
        public CommonEntry createFromParcel(Parcel source) {
            return new CommonEntry(source);
        }

        @Override
        public CommonEntry[] newArray(int size) {
            return new CommonEntry[size];
        }
    };
}
