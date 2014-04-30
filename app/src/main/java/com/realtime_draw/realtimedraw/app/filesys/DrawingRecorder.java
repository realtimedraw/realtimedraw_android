package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class DrawingRecorder {
    private ByteArrayOutputStream enc_out;
    private DrawingEncoder encoder;
    private long startTime;
    public DrawingRecorder(Bitmap canvasBitmap) {
        enc_out = new ByteArrayOutputStream();
        encoder = new DrawingEncoder(enc_out, canvasBitmap);
        encoder.start();
        startTime = System.currentTimeMillis();
    }

    public byte[] stop() throws Throwable {
        encoder.queueEOS();
        encoder.join();
        byte r[] = enc_out.toByteArray();
        finalize();
        return r;
    }

    public void addNowAction(DrawingAction action){
        encoder.queueAction((int) (System.currentTimeMillis() - startTime), action);
    }
}
