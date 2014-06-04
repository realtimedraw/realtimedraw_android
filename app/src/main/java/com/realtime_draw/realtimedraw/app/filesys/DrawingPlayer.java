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
        action.drawOnCanvas(canvas, state);
    }

    public void setCurrentFrame(Bitmap bitmap) {
        currentFrame = bitmap;
    }

    public Bitmap getCurrentFrame() {
        return currentFrame;
    }

    public void playGroup(DrawingFrameGroup group) throws Exception {
//        System.out.println("player: playing group...");
        if (group.isGroupEmpty()) {
            return;
        }
        while (System.currentTimeMillis() - startTime < group.getTimeIndex()) {
            Thread.sleep(5);
        }
        synchronized (this) {
            currentFrame = group.getKeyFrameBitmap();
            canvas = new Canvas(currentFrame);
            notify();
//            System.out.println("player: keyframe, notifying...");
        }
        for (DrawingFrame frame : group.getFrames()) {
            while (System.currentTimeMillis() - startTime < frame.getTimeIndex() + group.getTimeIndex()) {
                Thread.sleep(5);
            }
            synchronized (this) {
                playAction(frame.getDrawingAction());
                notify();
//                System.out.println("player: frame, notifying...");
            }
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
