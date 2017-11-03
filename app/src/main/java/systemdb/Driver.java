package systemdb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table "DRIVER".
 */
//@Entity
public class Driver implements Serializable {
    //private static final long serialVersionUID=1L;
    //@Id
    private Long id;
   // @NotNull
    private Integer driverid;
    private String name;
   // @NotNull
    private float driverlevel;
    //@NotNull
    private int drivercount;
    private String code;
    private String photo;
    private String phonenumber;
    //@NotNull
    private int driverage;
    //@NotNull
    private double distance;
    //@NotNull
    private double locationx;
   // @NotNull
    private double locationy;
    private String registrationid;
   // @NotNull
    private int todayOrderqty;
   // @NotNull
    private int monthOrderqty;
    private String drivergrade;
   // @Generated(hash = 2019472055)
    public Driver(Long id, Integer driverid, String name, float driverlevel, int drivercount,
                  String code, String photo, String phonenumber, int driverage, double distance,
                  double locationx, double locationy, String registrationid, int todayOrderqty,
                  int monthOrderqty, String drivergrade) {
        this.id = id;
        this.driverid = driverid;
        this.name = name;
        this.driverlevel = driverlevel;
        this.drivercount = drivercount;
        this.code = code;
        this.photo = photo;
        this.phonenumber = phonenumber;
        this.driverage = driverage;
        this.distance = distance;
        this.locationx = locationx;
        this.locationy = locationy;
        this.registrationid = registrationid;
        this.todayOrderqty = todayOrderqty;
        this.monthOrderqty = monthOrderqty;
        this.drivergrade = drivergrade;
    }
    //@Generated(hash = 911393595)
    public Driver() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getDriverid() {
        return this.driverid;
    }
    public void setDriverid(Integer driverid) {
        this.driverid = driverid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public float getDriverlevel() {
        return this.driverlevel;
    }
    public void setDriverlevel(float driverlevel) {
        this.driverlevel = driverlevel;
    }
    public int getDrivercount() {
        return this.drivercount;
    }
    public void setDrivercount(int drivercount) {
        this.drivercount = drivercount;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhonenumber() {
        return this.phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public int getDriverage() {
        return this.driverage;
    }
    public void setDriverage(int driverage) {
        this.driverage = driverage;
    }
    public double getDistance() {
        return this.distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public double getLocationx() {
        return this.locationx;
    }
    public void setLocationx(double locationx) {
        this.locationx = locationx;
    }
    public double getLocationy() {
        return this.locationy;
    }
    public void setLocationy(double locationy) {
        this.locationy = locationy;
    }
    public String getRegistrationid() {
        return this.registrationid;
    }
    public void setRegistrationid(String registrationid) {
        this.registrationid = registrationid;
    }
    public int getTodayOrderqty() {
        return this.todayOrderqty;
    }
    public void setTodayOrderqty(int todayOrderqty) {
        this.todayOrderqty = todayOrderqty;
    }
    public int getMonthOrderqty() {
        return this.monthOrderqty;
    }
    public void setMonthOrderqty(int monthOrderqty) {
        this.monthOrderqty = monthOrderqty;
    }
    public String getDrivergrade() {
        return this.drivergrade;
    }
    public void setDrivergrade(String drivergrade) {
        this.drivergrade = drivergrade;
    }

}
