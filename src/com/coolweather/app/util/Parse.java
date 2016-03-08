package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Parse {

/***************************************************************
 ********** *************解析省，市，县数据***************************
 */
	
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
	
/*************************************************************************
 *************************json解析***************************************
 */
	/**
	 * 解析处理器返回的json数据，并存储到本地
	 */
	public static void parseWeatherInfoJson(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String cityId = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String ptime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,cityId,temp1,temp2,weatherDesp,ptime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将天气信息存储到本地
	 */
	private static void saveWeatherInfo(Context context, String cityName, String cityId, String temp1, String temp2,
			String weatherDesp, String ptime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("city_name", cityName);
		editor.putBoolean("city_selected", true);
		editor.putString("city_id", cityId);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weatherDesp", weatherDesp);
		editor.putString("ptime", ptime);
		editor.putString("current_data", sdf.format(new Date()));
		editor.commit();
	}

	
}
