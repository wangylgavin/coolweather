package com.coolweather.app.util;
/*
 * ���������ص����������ؽ��
 */
public interface HttpCallbackListener {
	/*
	 * ������������ʱ���ã��Է������������ݽ��д���
	 */
	public void onFinish(String response);
	
	/*
	 * �����쳣ʱ���ã����쳣���д���
	 */
	public void onError(Exception e);
}
