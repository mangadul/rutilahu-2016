package com.alphamedia.rutilahu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class AmbilGambar extends Activity implements SurfaceHolder.Callback,
        OnClickListener {
    static final int FOTO_MODE = 0;
    private static final String TAG = "CameraTest";
    Camera mCamera;
    boolean mPreviewRunning = false;
    private Context mContext = this;
    private String namafile="";


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Log.e(TAG, "onCreate");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            namafile = extras.getString("namafile");
        }

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cameraview);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        mSurfaceView.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        private Activity ACT;
        private Context CON;
        public void onPictureTaken(byte[] imageData, Camera c) {

            if (imageData != null) {
                Intent mIntent = new Intent();
                try {
                    File file = new File(namafile);
                    Uri outputFileUri = Uri.fromFile(file);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    //options.inSampleSize = 5;
                    options.inSampleSize = 1;
                    Bitmap bmpPic = BitmapFactory.decodeFile(outputFileUri.getPath(), options);
                    while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                        options.inSampleSize++;
                        bmpPic = BitmapFactory.decodeFile(outputFileUri.getPath(), options);
                    }
                    options.inJustDecodeBounds = false;
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
                    Bitmap bitmap = bmpPic;
                    ExifInterface exif = new ExifInterface(outputFileUri.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    Matrix matrix = new Matrix();
                    switch(orientation){
                        case ExifInterface.ORIENTATION_NORMAL:  matrix.postRotate(0);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90 : matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180 : matrix.postRotate(180);
                            break;
                        default: matrix.postRotate(270);
                            break;
                    }
                    //assert bitmap != null;
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    FileOutputStream out = new FileOutputStream(outputFileUri.getPath());
                    //Bitmap e = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    out.write(imageData);
                    out.close();
                    Intent i = new Intent(ACT, MediaStore.class);
                    ACT.startActivity(i);
                    setResult(FOTO_MODE, mIntent);
                } catch (Exception e) {
                    ACT.finish();
                }
                SystemClock.sleep(100);
                mCamera.startPreview();
            }
        }
    };

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        mCamera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged");

        // XXX stopPreview() will crash if preview is not running
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }

        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(w, h);
        mCamera.setParameters(p);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        mCamera.startPreview();
        mPreviewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        mCamera.stopPreview();
        mPreviewRunning = false;
        mCamera.release();
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    public void onClick(View arg0) {

        mCamera.takePicture(null, mPictureCallback, mPictureCallback);

    }
}