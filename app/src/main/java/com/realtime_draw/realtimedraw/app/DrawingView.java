package com.realtime_draw.realtimedraw.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.realtime_draw.realtimedraw.app.filesys.DrawingActionClear;
import com.realtime_draw.realtimedraw.app.filesys.DrawingActionPickColor;
import com.realtime_draw.realtimedraw.app.filesys.DrawingActionUseCoord;
import com.realtime_draw.realtimedraw.app.filesys.DrawingPlayerState;
import com.realtime_draw.realtimedraw.app.filesys.DrawingRecorder;
import com.realtime_draw.realtimedraw.app.filesys.UseCoordEnum;

public class DrawingView extends View {
    private DrawingPlayerState state;
    private Canvas canvas;
    private Bitmap bitmap = null;
    private FullscreenActivity activity = null;
    public boolean isRecording = false;
    private DrawingRecorder recorder = new DrawingRecorder();
    private boolean paused = false;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        state = new DrawingPlayerState();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (bitmap != null) {
            return;
        }
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        if(isRecording)
            recorder.start();
        clearScreen();
        setColor("black");
    }

    public byte[] stopRecording() throws Throwable {
        return recorder.stop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawPath(state.path, state.paint);
    }


    //register user as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(paused)
            return false;
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                state.path.moveTo(touchX, touchY);
                if (isRecording)
                    recorder.addNowAction(new DrawingActionUseCoord((short) touchX, (short) touchY, UseCoordEnum.TOUCH_DOWN));
                break;
            case MotionEvent.ACTION_MOVE:
                state.path.lineTo(touchX, touchY);
                if (isRecording)
                    recorder.addNowAction(new DrawingActionUseCoord((short) touchX, (short) touchY, UseCoordEnum.TOUCH_MOVE));
                break;
            case MotionEvent.ACTION_UP:
                state.path.lineTo(touchX, touchY);
                canvas.drawPath(state.path, state.paint);
                state.path.reset();
                if (isRecording)
                    recorder.addNowAction(new DrawingActionUseCoord((short) touchX, (short) touchY, UseCoordEnum.TOUCH_UP));
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }


    public void setColor(String newColor) {//set color
        invalidate();
        int paintColor = Color.parseColor(newColor);
        if (isRecording)
            recorder.addNowAction(new DrawingActionPickColor(paintColor));
        state.paint.setColor(paintColor);
    }


    public void setBrushSize(short size) {
        state.paint.setStrokeWidth(size);
    }

    public short getBrushSize(){
        return (short) state.paint.getStrokeWidth();
    }

    public void clearScreen() {
        canvas.drawColor(state.paint.getColor());
        if(isRecording)
            recorder.addNowAction(new DrawingActionClear());
        invalidate();
    }


    public int getPaintAlpha() {
        return Math.round((float) state.paint.getAlpha() / 255 * 100);
    }


    public void setPaintAlpha(int newAlpha) {
        state.paint.setAlpha(Math.round((float) newAlpha / 100 * 255));
//        if(isRecording)
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public FullscreenActivity getActivity() {
        return activity;
    }

    public void setActivity(FullscreenActivity activity) {
        this.activity = activity;
    }

    public void setOnDraw(DrawingRecorder.onAddListener listener){
        recorder.setOnAddListener(listener);
    }

    public void pauseRecording() {
        recorder.pause();
        paused = true;
    }

    public void resumeRecording() {
        recorder.resume();
        paused = false;
    }
}

