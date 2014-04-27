package com.realtime_draw.realtimedraw.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.realtime_draw.realtimedraw.app.R;
import com.realtime_draw.realtimedraw.app.filesys.DrawingActionPickColor;
import com.realtime_draw.realtimedraw.app.filesys.DrawingActionUseCoord;
import com.realtime_draw.realtimedraw.app.filesys.DrawingDecoder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingEncoder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingFrameGroup;
import com.realtime_draw.realtimedraw.app.filesys.DrawingPlayer;
import com.realtime_draw.realtimedraw.app.filesys.DrawingRecorder;
import com.realtime_draw.realtimedraw.app.filesys.UseCoordEnum;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;

public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap = null;
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private int paintAlpha = 255;
    public boolean readonly = true;
    private FullscreenActivity activity = null;
    private InputStream in;
    public boolean isRecording = false;
    private DrawingRecorder recorder;

    final DrawingPlayer player = new DrawingPlayer();
    final Thread displayer = new Thread(new Runnable() {
        @Override
        public void run() {
            synchronized (player) {
                try {
                    while (true) {
                        player.wait();
                        canvasBitmap = player.getCurrentFrame();
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    });
    final Thread reader = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                DrawingDecoder decoder = new DrawingDecoder(in);
                activity.showToast("Started playing");
                player.reset();
                while (true) {
                    player.playGroup(decoder.getDrawingFrameGroup());
                }
            } catch (EOFException e) {
                activity.showToast("Finished playing");
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                activity.showToast("Error playing");
                e.printStackTrace();
            }
        }
    });

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        if (canvasBitmap != null) {
            return;
        }
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        clearScreen();
        if(isRecording){
            System.out.println(w+"x"+h);
            System.out.println(canvasBitmap);
            recorder = new DrawingRecorder(canvasBitmap);
        }
    }

    public byte[] stopRecording() throws Throwable {
        return recorder.stop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }


    //register user as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (readonly)
            return true;
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                if(isRecording)
                    recorder.addNowAction(new DrawingActionUseCoord((short)touchX, (short)touchY, UseCoordEnum.TOUCH_DOWN));
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                if(isRecording)
                    recorder.addNowAction(new DrawingActionUseCoord((short)touchX, (short)touchY, UseCoordEnum.TOUCH_MOVE));
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                if(isRecording)
                    recorder.addNowAction(new DrawingActionUseCoord((short)touchX, (short)touchY, UseCoordEnum.TOUCH_UP));
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }


    public void setColor(String newColor) {//set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        if(isRecording)
            recorder.addNowAction(new DrawingActionPickColor(paintColor));
        drawPaint.setColor(paintColor);
    }


    public void setBrushSize(float newSize) {//update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }


    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {//set erase true or false
        erase = isErase;
        if (erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }


    public void clearScreen() {
        drawCanvas.drawColor(Color.argb(255, 255, 255, 255));
        invalidate();
    }


    public int getPaintAlpha() {
        return Math.round((float) paintAlpha / 255 * 100);
    }


    public void setPaintAlpha(int newAlpha) {
        paintAlpha = Math.round((float) newAlpha / 100 * 255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public void play(final InputStream in) {
        this.in = in;
        reader.start();
        displayer.start();
    }

    public void stop(){
        displayer.interrupt();
        reader.interrupt();
    }

    public FullscreenActivity getActivity() {
        return activity;
    }

    public void setActivity(FullscreenActivity activity) {
        this.activity = activity;
    }
}

