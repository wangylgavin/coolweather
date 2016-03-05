package com.coolweather.app.util;
/*
 * 监听器，回调服务器返回结果
 */
public interface HttpCallbackListener {
	/*
	 * 返回正常数据时调用，对服务器返回数据进行处理
	 */
	public void onFinish(String response);
	
	/*
	 * 链接异常时调用，对异常进行处理
	 */
	public void onError(Exception e);
}
