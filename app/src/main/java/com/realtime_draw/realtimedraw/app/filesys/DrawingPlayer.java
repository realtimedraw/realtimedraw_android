package com.realtime_draw.realtimedraw.app.filesys;

import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DrawingPlayer {
    private Bitmap currentFrame = null;
    private Canvas canvas = null;
    private DrawingPlayerState state = new DrawingPlayerState();
    private long startTime;

    public void playAction(DrawingAction action) {
//        System.out.println(action);
        synchronized (this) {
            action.drawOnCanvas(canvas, state);
            notify();
        }
    }

    public void setCurrentFrame(Bitmap bitmap) {
        synchronized (this) {
            currentFrame = bitmap;
            canvas = new Canvas(currentFrame);
            notify();
        }
    }

    public Bitmap getCurrentFrame() {
        return currentFrame;
    }

    public void playGroup(DrawingFrameGroup group) throws Exception {
        if (group.isGroupEmpty()) {
            return;
        }
        while (System.currentTimeMillis() - startTime < group.getTimeIndex()) {
            Thread.sleep(5);
        }
        if(group.getKeyFrameBitmap()!=null){
            setCurrentFrame(group.getKeyFrameBitmap());
        }
        for (DrawingFrame frame : group.getFrames()) {
            while (System.currentTimeMillis() - startTime < frame.getTimeIndex() + group.getTimeIndex()) {
                Thread.sleep(5);
            }
            playAction(frame.getDrawingAction());
        }
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public DrawingPlayerState getState() {
        return state;
    }
}
