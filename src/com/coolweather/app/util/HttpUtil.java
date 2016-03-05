package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpUtil {
	
	/*
	 * ���ӷ��������ڼ������жԷ������ݽ��д���
	 */
	public static void sendHttpRequest(final String address, 
			final HttpCallbackListener listener) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null) {
						response.append(line);
					}
					
					if(listener != null) {
						//�ص�onFinish�������Է������������ݽ��д���
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener != null) {
						listener.onError(e);
					}
				}finally {
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
