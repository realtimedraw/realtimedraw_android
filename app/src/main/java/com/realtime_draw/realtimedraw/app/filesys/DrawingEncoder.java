package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingDeque;

public class DrawingEncoder extends Thread {
    public static final short keyFrameInterval = 10000;//10s
    private DataOutputStream out;
    private DrawingFrameGroup frameGroup = new DrawingFrameGroup();
    private DrawingPlayer player = new DrawingPlayer();
    private LinkedBlockingDeque<QueueItem> queue = new LinkedBlockingDeque<>();

    public DrawingEncoder(OutputStream outputStream, Bitmap initialFrame){
        super("DrawingEncoder");
        out = new DataOutputStream(outputStream);
    }

    public DrawingEncoder(OutputStream outputStream){
        this(outputStream, null);
    }

    public void queueAction(int timeIndex, DrawingAction action){
        queue.addLast(new QueueItem(timeIndex, action));
    }

    public void queueEOS(){
        queueAction(-1, null);
    }

    public synchronized void run() {
        try {
//            player.setCurrentFrame(initialFrame);
            frameGroup.setTimeIndex(0);
            while (true){
                QueueItem item;
                item = queue.takeFirst();
                if(item.timeIndex<0){
                    frameGroup.encode(out);
                    frameGroup = null;
                    return;
                }
                if(item.timeIndex - frameGroup.getTimeIndex() >= keyFrameInterval){
                    frameGroup.encode(out);
                    frameGroup = new DrawingFrameGroup(player.getCurrentFrame());
                    frameGroup.setTimeIndex(item.timeIndex);
                }
                frameGroup.appendFrame(new DrawingFrame((short)(item.timeIndex - frameGroup.getTimeIndex()), item.action));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class QueueItem {
        public int timeIndex;
        public DrawingAction action;
        public QueueItem(int timeIndex, DrawingAction action){
            this.timeIndex = timeIndex;
            this.action = action;
        }
    }
}
