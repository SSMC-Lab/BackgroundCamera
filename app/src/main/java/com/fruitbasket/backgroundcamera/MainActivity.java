package com.fruitbasket.backgroundcamera;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.backgroundcamera.R;

public class MainActivity extends Activity {
	
	private final static String TAG="MainActivity";
    private Button startTaking;
    private Button stopTaking;
    
    private AudioManager audio;
    private int currentMode;
    
    private Intent intentToService;
    private boolean mIsBound=false;
    private ServiceConnection connection=new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			Log.d(TAG,"onServiceContected");
			mIsBound=true;
			//start service after connected
			startService(intentToService);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG,"onServiceDisconnected");
			mIsBound=false;
		}
    	
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        initalizeWindows();
        setContentView(R.layout.activity_main);
        initializeViews();
        
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        currentMode = audio.getRingerMode();
        intentToService=new Intent(getBaseContext(),BackgroundCameraService.class);
        intentToService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
    	outState.putBoolean("mIsBound", mIsBound);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
    	mIsBound=savedInstanceState.getBoolean("mIsBound");
    }
    
    @Override
    public void onDestroy(){
    	Log.d(TAG,"onDestroy()");
    	super.onDestroy();
    }
    
    private void initalizeWindows(){
    	 getWindow().clearFlags(
                 WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void initializeViews(){
    	startTaking=(Button)this.findViewById(R.id.startTaking);
    	stopTaking=(Button)this.findViewById(R.id.stopTaking);
        
        cameraOnClickListener listener=new cameraOnClickListener();
        startTaking.setOnClickListener(listener);
        stopTaking.setOnClickListener(listener);
    }
    
    private void startTakingPhotos(){
    	//go into silent mode
        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    	if(mIsBound==false){
        	bindService(
        			intentToService,
	        		connection,
	        		Context.BIND_AUTO_CREATE);
        	//do not do  mIsBound==true;
        }
    }
    
    private void stopTakingPhotos(){
    	audio.setRingerMode(currentMode);
		if(mIsBound==true){
			unbindService(connection);
			//must set mIsBound=false
			mIsBound=false;
		}
    	stopService(new Intent(getBaseContext(),BackgroundCameraService.class));
    }
    
	
    
    
    class cameraOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch(view.getId()){
			case R.id.startTaking:
				Log.d(TAG,"startTaking clicked");
		        startTakingPhotos();
				break;
				
			case R.id.stopTaking:
				Log.d(TAG,"stopTaking clicked");
				stopTakingPhotos();
				break;
			}
		}
    }
}
