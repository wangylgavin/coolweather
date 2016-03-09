package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdataReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Parse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//Log.w("AutoUpdateService", "onStartCommand-->" + Thread.currentThread().getId());
				
				updataWeather(); 
			}
		}).start();
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		int anHour = 3*1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdataReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * ��������
	 */
	private void updataWeather() {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = spf.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Parse.parseWeatherInfoJson(AutoUpdateService.this, response);
				Log.w("AutoUpdateService", "��ʱ����-->"+response);
				//Log.w("AutoUpdateService", "onFinish-->" + Thread.currentThread().getId());
			}
			
			@Override
			public void onError(Exception e) {
				Log.w("AutoUpdateService", "��ʱ����ʧ��");
			}
		});
	}
}
