package com.fruitbasket.backgroundcamera;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.example.backgroundcamera.R;

public class BackgroundCameraService extends Service {

    private static final String TAG="BackgroundCameraService";

    private CameraControler mCameraControler;

    private LinearLayout mFloatLayout;
    private LayoutParams wmParams;
    private WindowManager mWindowManager;
    private SurfaceView mFloatSurfaceView;

    @Override
    public IBinder onBind(Intent arg0) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.d(TAG,"onStartCommand()");
        mCameraControler=new CameraControler(mFloatSurfaceView);///
        mCameraControler.startTakingPhotos();
		/*if(mCameraControler!=null){
			mCameraControler.startTakingPhotos();
		}
		else{
			Log.e(TAG,"cameraControler==null");
		}*/
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"onCreate()");
        createFloatView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        mCameraControler.stopTakingPhotos();
        mCameraControler.releaseCamera();
        if(mFloatLayout!=null){
            mWindowManager.removeView(mFloatLayout);
        }
    }

    /**
     *
     */
    private void createFloatView()
    {
        wmParams = new LayoutParams();
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags =
                //LayoutParams.FLAG_NOT_TOUCH_MODAL |
                LayoutParams.FLAG_NOT_FOCUSABLE
        //LayoutParams.FLAG_NOT_TOUCHABLE
        ;

        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_window, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
        Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
        Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop());
        Log.i(TAG, "mFloatLayout-->bottom" + mFloatLayout.getBottom());

        mFloatSurfaceView = (SurfaceView)mFloatLayout.findViewById(R.id.FloatSurfaceView);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }




    public class MyBinder extends Binder{
        public BackgroundCameraService getService(){
            return BackgroundCameraService.this;
        }
    }
}
