package com.realtime_draw.realtimedraw.app.filesys;

import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DrawingPlayer {
    private Bitmap currentFrame = null;
    private Canvas canvas = null;
    private DrawingPlayerState state = new DrawingPlayerState();
    private boolean locked = false;
    private long startTime;

    public synchronized void playAction(DrawingAction action){
        synchronized (this){
            System.out.println(action);
            action.drawOnCanvas(canvas, state);
        }
    }

    public synchronized void setCurrentFrame(Bitmap bitmap){
        synchronized (this){
            currentFrame = bitmap;
        }
    }

    public synchronized Bitmap getCurrentFrame() {
        synchronized (this){
            return currentFrame;
        }
    }

    public synchronized void playGroup(DrawingFrameGroup group) throws Exception {
        if(group.isGroupEmpty()){
            return;
        }
        while(System.currentTimeMillis() - startTime < group.getTimeIndex()){
            Thread.sleep(5);
        }
        currentFrame = group.getKeyFrame().copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(currentFrame);
        notify();
        for(DrawingFrame frame : group.getFrames()){
            while(System.currentTimeMillis() - startTime < frame.getTimeIndex()){
                Thread.sleep(5);
            }
            playAction(frame.getDrawingAction());
            notify();
        }
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }
}
