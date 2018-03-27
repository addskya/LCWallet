package com.lc.app.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.lc.app.BR;

import java.io.Serializable;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
public class Account extends BaseObservable
        implements Serializable, Parcelable {

    public static final int serialVersionUID = 0x00000001;

    // 钱包名称
    private String walletName;

    // 钱包密码
    private String password;

    // 钱包地址
    private String address;

    // 钱包密钥
    private String keystore;

    // 钱包余额
    private float remain;

    private transient boolean secret = true;

    public Account() {
    }

    @Bindable
    public String getWalletName() {
        return walletName;
    }


    public void setWalletName(String walletName) {
        this.walletName = walletName;
        notifyPropertyChanged(BR.walletName);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getAddress() {
        if (!secret) {
            return address;
        } else {
            StringBuilder sb = new StringBuilder(address);
            sb.replace(sb.length() - 10, sb.length(), "**********");
            return sb.toString();
        }
    }

    public String getRealAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    @Bindable
    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
    }

    @Override
    public String toString() {
        return "Account{" +
                "walletName='" + walletName + '\'' +
                ", password='" + password + '\'' +
                ", keystore='" + keystore + '\'' +
                ", address='" + address + '\'' +
                ", remain=" + remain +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Account) {
            return !TextUtils.isEmpty(address) &&
                    address.equalsIgnoreCase(((Account)obj).getRealAddress());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.walletName);
        dest.writeString(this.password);
        dest.writeString(this.keystore);
        dest.writeString(this.address);
        dest.writeFloat(this.remain);
    }

    protected Account(Parcel in) {
        this.walletName = in.readString();
        this.password = in.readString();
        this.keystore = in.readString();
        this.address = in.readString();
        this.remain = in.readFloat();
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
