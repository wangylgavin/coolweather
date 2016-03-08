package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Parse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private TextView cityName, publishText, weatherDesp, temp1, temp2,currentData;
	private LinearLayout weatherInfo;
	private Button switchCity,refresh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		cityName = (TextView)findViewById(R.id.city_name);
		publishText = (TextView)findViewById(R.id.publish_text);
		weatherDesp = (TextView)findViewById(R.id.weather_desp);
		temp1 = (TextView)findViewById(R.id.temp1);
		temp2 = (TextView)findViewById(R.id.temp2);
		currentData = (TextView)findViewById(R.id.current_data);
		weatherInfo = (LinearLayout)findViewById(R.id.weather_info_layout);
		refresh = (Button)findViewById(R.id.refresh_weather);
		switchCity = (Button)findViewById(R.id.switch_city);
		refresh.setOnClickListener(this);
		switchCity.setOnClickListener(this);
		
		String countyCode = getIntent().getStringExtra("countyCode");
		if(TextUtils.isEmpty(countyCode)) {
			showWeather();
		}else{
			publishText.setText("同步中");
			weatherInfo.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}
	}
	
	/**
	 * 根据天气代号，查询天气
	 * @param countyCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * 根据城市代号，查询天气代号
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	
	/**
	 * 根据类型去服务器查询天气代号或信息
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)) {
					String[] array = response.split("\\|");
					if(array != null && array.length ==2)
						queryWeatherInfo(array[1]);
				}else if("weatherCode".equals(type)){
					Parse.parseWeatherInfoJson(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取信息，显示本地天气
	 */
	private void showWeather() {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(spf.getString("city_name", ""));
		publishText.setText("今天" + spf.getString("ptime", "") + "发布");
		weatherDesp.setText(spf.getString("weatherDesp", ""));
		temp1.setText(spf.getString("temp1", ""));
		temp2.setText(spf.getString("temp2", ""));
		currentData.setText(spf.getString("current_data", ""));
		weatherInfo.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("form_switch_city", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中");
			SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = spf.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
				Log.w("WeatherActivity", "更新天气");
			}
			
			break;

		default:
			break;
		}
	}


}
