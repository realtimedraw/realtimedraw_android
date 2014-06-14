package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DrawingRecorder {
    private ByteArrayOutputStream enc_out;
    private DrawingEncoder encoder;
    private long startTime;
    private onAddListener listener_ = null;
    private long pausedTime = 0;
    private long lastPause = 0;

    public void start(Bitmap canvasBitmap){
        Log.d("xefa", "aed");
        enc_out = new ByteArrayOutputStream();
        encoder = new DrawingEncoder(enc_out, canvasBitmap);
        //encoder.start();
        startTime = System.currentTimeMillis();
    }

    public void start(){
        start(null);
    }

    public byte[] stop() throws Throwable {
        encoder.start();
        encoder.queueEOS();
        encoder.join();
        return enc_out.toByteArray();
    }

    public void addNowAction(DrawingAction action){
        int timeIndex = (int) (System.currentTimeMillis() - startTime - pausedTime);
        encoder.queueAction(timeIndex, action);
        if(listener_ != null)
            listener_.onAdd(timeIndex, action);
    }

    public void setOnAddListener(onAddListener listener){
        listener_ = listener;
    }

    public void pause() {
        lastPause = System.currentTimeMillis();
    }

    public void resume() {
        pausedTime += (System.currentTimeMillis()-lastPause);
        lastPause = 0;
    }

    public static interface onAddListener{
        void onAdd(int timeIndex, DrawingAction action);
    }
}
