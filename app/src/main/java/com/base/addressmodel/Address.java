package com.base.addressmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/1/11.
 */

public class Address implements Parcelable {
    private Area province;
    private Area city;
    private Area district;

    public Area getProvince() {
        return province;
    }

    public void setProvince(Area province) {
        this.province = province;
    }

    public Area getCity() {
        return city;
    }

    public void setCity(Area city) {
        this.city = city;
    }

    public Area getDistrict() {
        return district;
    }

    public void setDistrict(Area district) {
        this.district = district;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.province);
        dest.writeSerializable(this.city);
        dest.writeSerializable(this.district);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.province = (Area) in.readSerializable();
        this.city = (Area) in.readSerializable();
        this.district = (Area) in.readSerializable();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
