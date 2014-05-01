package com.realtime_draw.realtimedraw.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.realtime_draw.realtimedraw.app.filesys.DrawingAction;
import com.realtime_draw.realtimedraw.app.filesys.DrawingDecoder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingPlayer;

import java.io.EOFException;
import java.io.InputStream;

public class WatchingView extends View {
    private boolean passive = false;
    final private FullscreenActivity activity = (FullscreenActivity) getContext();
    private InputStream in;
    private boolean playing = false;
    private boolean finished = false;
    private Bitmap bitmap = null;
    private Paint paint;
    private Path path;
    private int currentW, currentH;
    final private Object lock = new Object();
    final private DrawingPlayer player = new DrawingPlayer();
    final private Thread displayer = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (player) {
//                        System.out.println("displayer: waiting for player...");
                        player.wait();
//                        System.out.println("displayer: player notified");
                        bitmap = player.getCurrentFrame();
                        paint = player.getState().paint;
                        path = player.getState().path;
                        synchronized (lock) {
//                            System.out.println("displayer: waiting for view...");
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    invalidate();
                                }
                            });
                            lock.wait();
//                            System.out.println("displayer: view notified");
                        }
                    }
                }
            } catch (InterruptedException e) {
            }
        }

    }, "Displayer");
    private Thread reader = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                DrawingDecoder decoder = new DrawingDecoder(in);
                activity.showToast("Started playing");
                player.reset();
                while (true) {
//                    System.out.println("playgroup");
                    player.playGroup(decoder.getDrawingFrameGroup());
                }
            } catch (EOFException e) {
                activity.showToast("Finished playing");
                finished = true;
                displayer.interrupt();
                activity.togglePlayButton(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            } catch (InterruptedException e) {
            } catch (Exception e) {
                activity.showToast("Error playing");
                e.printStackTrace();
            }
        }
    }, "Watching reader");


    public WatchingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap == null) {
            super.onDraw(canvas);
            return;
        }
        synchronized (lock) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.drawPath(path, paint);
            lock.notify();
        }
    }

    public void play(InputStream in) {
        this.in = in;
        activity.togglePlayButton(getResources().getDrawable(android.R.drawable.ic_media_pause));
        playing = true;
        finished = false;
        reader.start();
        displayer.start();
        passive = false;
    }

    public void passivePlay(){
        activity.togglePlayButton(getResources().getDrawable(android.R.drawable.ic_media_pause));
        playing = true;
        finished = false;
        displayer.start();
        passive = true;
    }

    public void pause() {
        if (finished)
            return;
        playing = !playing;
        if (playing) {
            activity.togglePlayButton(getResources().getDrawable(android.R.drawable.ic_media_pause));
        } else {
            activity.togglePlayButton(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
    }

    public void stop() {
        displayer.interrupt();
        reader.interrupt();
        finished = true;
    }

//    public void setActivity(FullscreenActivity activity) {
//        this.activity = activity;
//    }

    public boolean isFinished(){
        return finished;
    }

    public void playAction(final DrawingAction action) {
        player.playAction(action);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        currentW = w;
        currentH = h;
        if(passive)
            player.setCurrentFrame(Bitmap.createBitmap(currentW, currentH, Bitmap.Config.ARGB_8888));
    }
}
