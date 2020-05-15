package com.base.eventBus;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/9.
 */

public class NetEvent<T> implements Parcelable , Serializable {
    private Integer status;
    private String msg;
    private Object event;
    private String EventType;
    private Integer tag;
    private Integer arg;
    private Integer arg0;
    public NetEvent<T> setEventType(String eventType) {
        EventType = eventType;
        return this;
    }
    public String getEventType(){
        if(event!=null){
            return event.getClass().getName();
        }
        return EventType;
    }
    public Integer getStatus() {
        return status;
    }

    public NetEvent<T> setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public T getEvent() {
        return (T)event;
    }

    public NetEvent<T> setEvent(Object event) {
        this.event = event;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public NetEvent<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }
    public Integer getTag() {
        return tag;
    }

    public NetEvent<T> setTag(Integer tag) {
        this.tag = tag;
        return this;
    }

    public Integer getArg() {
        return arg;
    }

    public NetEvent<T> setArg(Integer arg) {
        this.arg = arg;
        return this;
    }

    public Integer getArg0() {
        return arg0;
    }

    public NetEvent<T> setArg0(Integer arg0) {
        this.arg0 = arg0;
        return this;
    }

    public NetEvent() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.status);
        dest.writeString(this.msg);
        if (event != null) {
            try {
                Parcelable p = (Parcelable)event;
                dest.writeInt(1);
                dest.writeParcelable(p, flags);
            } catch (ClassCastException e) {
                throw new RuntimeException(
                        "Can't marshal non-Parcelable objects across processes.");
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeString(this.EventType);
        dest.writeValue(this.tag);
        dest.writeValue(this.arg);
        dest.writeValue(this.arg0);
    }

    protected NetEvent(Parcel in) {
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.msg = in.readString();
        this.event = in.readParcelable(Object.class.getClassLoader());
        this.EventType =  in.readString();
        this.tag = (Integer) in.readValue(Integer.class.getClassLoader());
        this.arg = (Integer) in.readValue(Integer.class.getClassLoader());
        this.arg0 = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<NetEvent> CREATOR = new Creator<NetEvent>() {
        @Override
        public NetEvent createFromParcel(Parcel source) {
            return new NetEvent(source);
        }

        @Override
        public NetEvent[] newArray(int size) {
            return new NetEvent[size];
        }
    };
    //深度复制 需要实现 Serializable
    public Object deepclone()  {
        try {
            //将对象写到流里
            ByteArrayOutputStream bo=new ByteArrayOutputStream();
            ObjectOutputStream oo=new ObjectOutputStream(bo);
            oo.writeObject(this);//从流里读出来
            ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi=new ObjectInputStream(bi);
            return(oi.readObject());
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
