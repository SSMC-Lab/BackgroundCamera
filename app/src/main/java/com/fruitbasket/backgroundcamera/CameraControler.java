package com.fruitbasket.backgroundcamera;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ShutterCallback;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

 
public class CameraControler{
	
	private final static String TAG="CameraControler";
	
    private SurfaceHolder holder;
    private SurfaceView mSurfaceView;
    
    private Camera camera;
    private Camera.Parameters parameters;
    private Timer mTimer;
    
    private MyAutoFocusCallback myAutoFocusCallback;
    private MySurfaceHolderCallback mySurfaceHolderCallback;
    private MyCameraPictureCallback myCameraPictureCallback;
    
    private int mInterval=5000;//ms
 
    public CameraControler(SurfaceView surfaceView) {
        Log.d(TAG,"CameraControler construted");
        
        myCameraPictureCallback=new MyCameraPictureCallback();
        myAutoFocusCallback=new MyAutoFocusCallback();
        mySurfaceHolderCallback=new MySurfaceHolderCallback();

        updateSurfaceView(surfaceView);
        mTimer=new Timer();
        initCamera();
    }

    public void updateSurfaceView(SurfaceView surfaceView){
    	mSurfaceView=surfaceView;
        if(mSurfaceView==null){
        	Log.e(TAG,"surfaceView==null");
        }
        else{
            Log.d(TAG,"surfaceView!=null");
        }
        holder = mSurfaceView.getHolder();
        holder.addCallback(mySurfaceHolderCallback);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void setInterval(int interval){
    	mInterval=interval;
    }
    
    private void initCamera(){
    	int frontCameraId=-1;
    	int numberOfCameras=Camera.getNumberOfCameras();
    	CameraInfo cameraInfo=new CameraInfo();
    	for(int i=0;i<numberOfCameras;i++){
    		Camera.getCameraInfo(i, cameraInfo);
    		if(cameraInfo.facing==CameraInfo.CAMERA_FACING_FRONT){
    			frontCameraId=i;
    		}
    	}
    	if(frontCameraId==-1){
    		if(numberOfCameras>0){
    			frontCameraId=0;
    		}
    		else{
    			Log.e(TAG,"no camera can open!");
    			return;///
    		}
    	}
    	
    	camera = Camera.open(frontCameraId);
   	    if (holder != null) {
            try {
                camera.setPreviewDisplay(holder);
                parameters = camera.getParameters();
                camera.setParameters(parameters);
                camera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void releaseCamera(){
    	if(camera!=null){
    		camera.release();
    		camera=null;
    	}
    }
    
    public void startTakingPhotos(){
    	Log.d(TAG,"startTakingPhotos()");
    	if(camera!=null){
    		Log.d(TAG,"camera!=null");
        	mTimer.scheduleAtFixedRate(new TimerTask(){

    			@Override
    			public void run() {
    				Log.d(TAG,"takePhotosRepeatedly() run()");
    				if(camera==null){
    					Log.d(TAG,"camera==null");
    				}
    				else{
    					Log.d(TAG,"camera!=null");
    				}

    				camera.autoFocus(myAutoFocusCallback);
    			}
        		
        	}, 0, mInterval);
    	}
    	else{
    		Log.d(TAG,"camera==null");
    	}
    	Log.d(TAG,"startTakingPhotos() ended");
    }

    public void stopTakingPhotos(){
    	if(mTimer!=null){
    		mTimer.cancel();
    	}
    }
    
    
    
    
    
    class MyCameraPictureCallback implements Camera.PictureCallback{
    	/**
         * callback this method after picture taken
         */
        public void onPictureTaken(byte[] data, Camera camera) {// ������ɺ󱣴���Ƭ
        	Log.i(TAG,"onPictureTaken()");

            File file = new File(Environment.getExternalStorageDirectory()
                    + "/backgroundCamera");
            file.mkdirs();

            try {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String time = format.format(date);
                String path = Environment.getExternalStorageDirectory()
                        + "/backgroundCamera/" + time + ".jpg";
                Utilities.data2File(data, path);
                camera.stopPreview();
                camera.startPreview();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    }
    
    class MyAutoFocusCallback implements AutoFocusCallback{

    	@Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.d(TAG,"onAutoFocus()");
            if (success) {
                Log.d(TAG,"onAutofocus() success==true");
            } else {
                Log.e(TAG,"onAutofocus() success==false");
            }

            camera.takePicture(new ShutterCallback() {// ����۽��ɹ����������
                @Override
                public void onShutter() {
                }
            }, null, myCameraPictureCallback);
        }
    }
    
    class MySurfaceHolderCallback implements SurfaceHolder.Callback{
    	public void surfaceCreated(final SurfaceHolder holder) {
        	Log.d(TAG,"surfaceCreated()");	
        }
     
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
        	Log.d(TAG,"surfaceChanged()");
            /*parameters = camera.getParameters();
            camera.setParameters(parameters);// ���ò���
            camera.startPreview();// ��ʼԤ��*/ 
       }
     
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        	Log.d(TAG,"surfaceDestroyed()");
        } 
    }

}
