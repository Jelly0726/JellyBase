package com.base.httpmvp.databean;

import android.os.Parcel;
import android.os.Parcelable;

public class Goods implements Parcelable {
    private int id;//商品id
    private String names;//商品名
    private String salesPrice;//折后价
    private String showPrice;//折前折
    private String LiJian;//立减
    private String Stock;//库存
    private String unit;//单位
    private String num;//库存
    private String showImgs;//图片
    private String sort;//分类
    private String remark;//说明
    private String xiangqingImg;//详情图文
    private String CanShuImg;//参数
    private int count=1;//购买数量

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(String showPrice) {
        this.showPrice = showPrice;
    }

    public String getLiJian() {
        return LiJian;
    }

    public void setLiJian(String liJian) {
        LiJian = liJian;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getShowImgs() {
        return showImgs;
    }

    public void setShowImgs(String showImgs) {
        this.showImgs = showImgs;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Goods() {
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getXiangqingImg() {
        return xiangqingImg;
    }

    public void setXiangqingImg(String xiangqingImg) {
        this.xiangqingImg = xiangqingImg;
    }

    public String getCanShuImg() {
        return CanShuImg;
    }

    public void setCanShuImg(String canShuImg) {
        CanShuImg = canShuImg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.names);
        dest.writeString(this.salesPrice);
        dest.writeString(this.showPrice);
        dest.writeString(this.LiJian);
        dest.writeString(this.Stock);
        dest.writeString(this.unit);
        dest.writeString(this.num);
        dest.writeString(this.showImgs);
        dest.writeString(this.sort);
        dest.writeString(this.remark);
        dest.writeString(this.xiangqingImg);
        dest.writeString(this.CanShuImg);
        dest.writeInt(this.count);
    }

    protected Goods(Parcel in) {
        this.id = in.readInt();
        this.names = in.readString();
        this.salesPrice = in.readString();
        this.showPrice = in.readString();
        this.LiJian = in.readString();
        this.Stock = in.readString();
        this.unit = in.readString();
        this.num = in.readString();
        this.showImgs = in.readString();
        this.sort = in.readString();
        this.remark = in.readString();
        this.xiangqingImg = in.readString();
        this.CanShuImg = in.readString();
        this.count = in.readInt();
    }

    public static final Creator<Goods> CREATOR = new Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel source) {
            return new Goods(source);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
}
