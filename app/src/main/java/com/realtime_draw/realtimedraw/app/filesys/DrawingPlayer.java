package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DrawingPlayer {
    private Bitmap currentFrame;
    private Canvas canvas;
    private DrawingPlayerState state = new DrawingPlayerState();
    private boolean locked = false;
    public DrawingPlayer(Bitmap initialFrame){
        currentFrame = initialFrame;
        canvas = new Canvas(currentFrame);
    }

    public synchronized void playAction(DrawingActionInterface action){
        synchronizedAccess(action);
    }

    public synchronized Bitmap getCurrentFrame() {
        return synchronizedAccess(null);
    }

    private synchronized Bitmap synchronizedAccess(DrawingActionInterface action){
        if(action == null)
            return Bitmap.createBitmap(currentFrame);
        action.drawOnCanvas(canvas, state);
        return null;
    }
}
