package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Parse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ListView listView;
	private TextView textView;
	
	/**
	 * 存放listView控件中要显示的数据
	 */
	private List<String> dataList;
	
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	
	/**
	 * 市列表
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<County> countyList;
	
	/**
	 * 数据库操作类
	 */
	private CoolWeatherDB db;
	private ProgressDialog progressDialog;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		listView = (ListView)findViewById(R.id.list_view);
		textView = (TextView)findViewById(R.id.title_text);
		dataList = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
		db = CoolWeatherDB.getInstance(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				if(currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if(currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}
				
			}
		});
		queryProvinces();
	}
/*******************************************************************
 * 从数据库或服务器上加载省市县数据
 */
	
	
	/**
	 * 查询全国所有省，优先从数据库查询
	 */
	private void queryProvinces() {
		provinceList = db.loadProvinces();
		if(!provinceList.isEmpty()) {
			dataList.clear();  //不能少
			for(Province p : provinceList) {
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else {
			queryFromServer(null, "province");
		}
	}
	
	
	/**
	 * 查询某省的所有市，优先从数据库查询
	 */
	private void queryCities() {
		
		cityList = db.loadCities(selectedProvince.getId());
		if(!cityList.isEmpty()) {
			
			dataList.clear();  //不能少
			for(City c : cityList) {
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	/**
	 * 查询某市的所有县，优先从数据库查询
	 */
	private void queryCounties() {
		
		countyList = db.loadCounties(selectedCity.getId());
		if(!countyList.isEmpty()) {
			
			dataList.clear();  //不能少
			for(County c : countyList) {
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/*
	 * 从服务器查询省市县数据
	 */
	private void queryFromServer(final String code, final String type) {
		Log.w("ChooseAreaActivity", "form server");
		String address;
		if(TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)) {
					result = Parse.parseProvinceResponse(db, response);
				}else if("city".equals(type)){
					result = Parse.parseCityResponse(db, response, selectedProvince.getId());
				}else if("county".equals(type)) {
					result = Parse.parseCountyResponse(db, response, selectedCity.getId());
				}
				
				if(result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)) {
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
								"加载失败", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	
	/**
	 * 显示正在下载
	 */
	private void showProgressDialog() {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("loading");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog() {
		if(progressDialog != null) {
			progressDialog.dismiss();
		}
		
	}

	/**
	 * back按键,（一个activity）
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY) {
			queryCities();
		}else if(currentLevel == LEVEL_CITY) {
			queryProvinces();
		}else{
			finish();
		}
	}
}
