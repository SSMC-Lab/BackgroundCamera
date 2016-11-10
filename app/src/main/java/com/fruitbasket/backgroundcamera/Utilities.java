package com.fruitbasket.backgroundcamera;

import java.io.FileOutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public final class Utilities {
	
	private Utilities(){}

	public static void data2File(byte[] w, String fileName) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(w);
            out.close();
        } catch (Exception e) {
            if (out != null)
                out.close(); 
            throw e;
        }
    }
	
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
