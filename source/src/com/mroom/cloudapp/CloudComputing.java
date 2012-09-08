package com.mroom.cloudapp;

import android.app.Application;
import android.util.Log;

public class CloudComputing extends Application {
	private static boolean DEBUG = true;
	private static CloudComputing instance;
	public static int nDownloadedFiles = 2000;
	
	public void onCreate() {
		super.onCreate();
		instance = this;
		
	}
	public static void i(String tag, String message){
		if( DEBUG ) {
			Log.i(tag, message);
		}
	}
	
	public static void e(String tag, String message) {
		if( DEBUG ) {
			Log.e(tag, message);
		}
	}
	
	public static CloudComputing getInstance() {
		return instance;
	}
}
