package com.coolweather.app.util;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.text.TextUtils;

public class Parse {

/***************************************************************
 ********** *************解析省，市，县数据*****************************/
	
	/**
	 * 解析和处理服务器返回的省级数据
	 */
	
	public static boolean parseProvinceResponse(CoolWeatherDB db, String response) {
		if(!TextUtils.isEmpty(response)) {
			String[] data = response.split(",");
			if(data != null && data.length >0) {
				for(String p : data) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					db.saveProvince(province);
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean parseCityResponse(CoolWeatherDB db, String response, int provinceId) {
		if(!TextUtils.isEmpty(response)) {
			String[] data = response.split(",");
			if(data != null && data.length >0) {
				for(String c : data) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					db.saveCity(city);
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean parseCountyResponse(CoolWeatherDB db, String response, int cityId) {
		if(!TextUtils.isEmpty(response)) {
			String[] data = response.split(",");
			if(data != null && data.length >0) {
				for(String c : data) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					db.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}
}
