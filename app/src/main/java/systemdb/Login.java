package systemdb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Entity mapped to table "ACTION_LOGIN".
 */
@Entity
public class Login implements Serializable {
    private static final long serialVersionUID=1L;
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    @SerializedName(value ="saleid", alternate = "userid")
    private Integer userID=-1;
    private String code;
    private String name;
    private String phone;
    private String nickname;
    @NotNull
    private Integer age=0;
    private String sex;
    @NotNull
    private float balance=0.0f;

    @Transient// 不存储在数据库中
    @SerializedName(value ="token")
    private String token;

    @NotNull
    @Transient// 不存储在数据库中
    @SerializedName(value ="tokenExpirationTime")
    private int tokenExpirationTime=0;

    @NotNull
    @Transient// 不存储在数据库中
    @SerializedName(value ="createTime")
    private long createTime=0l;

    @Generated(hash = 1918001279)
    public Login(Long id, @NotNull Integer userID, String code, String name, String phone,
            String nickname, @NotNull Integer age, String sex, float balance) {
        this.id = id;
        this.userID = userID;
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.nickname = nickname;
        this.age = age;
        this.sex = sex;
        this.balance = balance;
    }

    @Generated(hash = 1827378950)
    public Login() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserID() {
        return this.userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public float getBalance() {
        return this.balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(int tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
