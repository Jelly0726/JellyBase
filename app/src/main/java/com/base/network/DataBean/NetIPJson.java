package com.base.network.DataBean;

import java.io.Serializable;

/**
 * Created by BYPC006 on 2017/5/15.
 */

public class NetIPJson implements Serializable {

	/**
	 * {"code":0,"data":{"country":"\u4e2d\u56fd","country_id":"CN","area":"\u534e\u4e1c",
	 * "area_id":"300000","region":"\u798f\u5efa\u7701","region_id":"350000","city":"\u53a6\u95e8\u5e02",
	 * "city_id":"350200","county":"","county_id":"-1","isp":"\u7535\u4fe1","isp_id":"100017",
	 * "ip":"110.87.107.120"}}
	 */

	private int code;
	private DataBean data;
	public int getStatus() {
		return code;
	}
	public void setStatus(int code) {
		this.code = code;
	}
	public DataBean getData() {
		return data;
	}
	public void setData(DataBean data) {
		this.data = data;
	}
	public static class DataBean implements Serializable {
		private String country;
		private String country_id;
		private String area;
		private String area_id;
		private String region;
		private String region_id;
		private String city;
		private String city_id;
		private String county;
		private String county_id;
		private String isp;
		private String isp_id;
		private String ip;

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getCountry_id() {
			return country_id;
		}

		public void setCountry_id(String country_id) {
			this.country_id = country_id;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getArea_id() {
			return area_id;
		}

		public void setArea_id(String area_id) {
			this.area_id = area_id;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getRegion_id() {
			return region_id;
		}

		public void setRegion_id(String region_id) {
			this.region_id = region_id;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getCity_id() {
			return city_id;
		}

		public void setCity_id(String city_id) {
			this.city_id = city_id;
		}

		public String getCounty() {
			return county;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		public String getCounty_id() {
			return county_id;
		}

		public void setCounty_id(String county_id) {
			this.county_id = county_id;
		}

		public String getIsp() {
			return isp;
		}

		public void setIsp(String isp) {
			this.isp = isp;
		}

		public String getIsp_id() {
			return isp_id;
		}

		public void setIsp_id(String isp_id) {
			this.isp_id = isp_id;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}
	}
}
