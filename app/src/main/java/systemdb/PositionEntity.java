package systemdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

/**
 * ClassName:PositionEntity <br/>
 * Function: 封装的关于位置的实体 <br/>
 * Date: 2015年4月3日 上午9:50:28 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
@Entity
public class PositionEntity implements Serializable {
	private static final long serialVersionUID=1L;
	@Id(autoincrement = true)
	public Long id;
	@NotNull
	public double latitue=0d;
	@NotNull
	public double longitude=0d;

	public String address;

	public String city;
	public String adCode;
	public String district;
	public int type=0;
	public int from=0;
	public PositionEntity(double latitude, double longtitude, String address,String city) {
		this.latitue = latitude;
		this.longitude = longtitude;
		this.address = address;
		this.city=city;
	}
	@Generated(hash = 1532927154)
	public PositionEntity(Long id, double latitue, double longitude, String address,
						  String city, String adCode, String district, int type, int from) {
		this.id = id;
		this.latitue = latitue;
		this.longitude = longitude;
		this.address = address;
		this.city = city;
		this.adCode = adCode;
		this.district = district;
		this.type = type;
		this.from = from;
	}
	@Generated(hash = 1547125250)
	public PositionEntity() {
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getLatitue() {
		return this.latitue;
	}
	public void setLatitue(double latitue) {
		this.latitue = latitue;
	}
	public double getLongitude() {
		return this.longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return this.address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAdCode() {
		return this.adCode;
	}
	public void setAdCode(String adCode) {
		this.adCode = adCode;
	}
	public String getDistrict() {
		return this.district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public int getType() {
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getFrom() {
		return this.from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
}
