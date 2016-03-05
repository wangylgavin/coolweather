package com.coolweather.app.Test;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestHttp extends AndroidTestCase{
	
	public void testHttpRequest() {
		String address = "http://www.weather.com.cn/data/list3/city.xml";
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Log.e("TestHttp", response);
			}
			
			@Override
			public void onError(Exception e) {
				Log.e("TestHttp", "error");
			}
		});
	}
}
