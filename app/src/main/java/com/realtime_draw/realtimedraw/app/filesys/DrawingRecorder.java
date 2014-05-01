package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DrawingRecorder {
    private ByteArrayOutputStream enc_out;
    private DrawingEncoder encoder;
    private long startTime;
    private onAddListener listener_ = null;

    public void start(Bitmap canvasBitmap){
        enc_out = new ByteArrayOutputStream();
        encoder = new DrawingEncoder(enc_out, canvasBitmap);
        //encoder.start();
        startTime = System.currentTimeMillis();
    }

    public byte[] stop() throws Throwable {
        encoder.start();
        encoder.queueEOS();
        encoder.join();
        return enc_out.toByteArray();
    }

    public void addNowAction(DrawingAction action){
        int timeIndex = (int) (System.currentTimeMillis() - startTime);
        encoder.queueAction(timeIndex, action);
        if(listener_ != null)
            listener_.onAdd(timeIndex, action);
    }

    public void setOnAddListener(onAddListener listener){
        listener_ = listener;
    }

    public static interface onAddListener{
        void onAdd(int timeIndex, DrawingAction action);
    }
}
