package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DrawingPlayer {
    private Bitmap currentFrame;
    private Canvas canvas;
    DrawingPlayerState state = new DrawingPlayerState();
    public DrawingPlayer(Bitmap initialFrame){
        currentFrame = initialFrame;
        canvas = new Canvas(currentFrame);
    }

    public synchronized void playAction(DrawingActionInterface action){
        action.drawOnCanvas(canvas, state);
    }

    public synchronized Bitmap getCurrentFrame(){
        return currentFrame;
    }
}
