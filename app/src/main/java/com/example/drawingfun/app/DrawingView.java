package com.example.drawingfun.app;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;




public class DrawingView extends View 
{
	
	//drawing path
	private Path drawPath;
	//drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	//initial color
	private int paintColor = 0xFF660000;
	//canvas
	private Canvas drawCanvas;
	//canvas bitmap
	private Bitmap canvasBitmap;
	private float brushSize, lastBrushSize;
	private boolean erase=false;
	private int paintAlpha = 255;
	
	
	
	
	public DrawingView(Context context, AttributeSet attrs)
	  {super(context, attrs);
	    setupDrawing();
	   }

	private void setupDrawing()
	{   
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
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
	 //view given size
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
	 //draw view
	 canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
	 canvas.drawPath(drawPath, drawPaint);
	}
	
	
	//register user as drawing action
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
	 //detect user touch  
	 float touchX = event.getX();
	 float touchY = event.getY();
	 
	 switch (event.getAction()) 
	 {
	 case MotionEvent.ACTION_DOWN:
	     drawPath.moveTo(touchX, touchY);
	     break;
	 case MotionEvent.ACTION_MOVE:
	     drawPath.lineTo(touchX, touchY);
	     break;
	 case MotionEvent.ACTION_UP:
		 drawPath.lineTo(touchX, touchY);
	     drawCanvas.drawPath(drawPath, drawPaint);
	     drawPath.reset();
	     break;
	 default:
	     return false;
	 }
	 
	 invalidate();
	 return true;
	}
	
	
	public void setColor(String newColor)
	{//set color
	  invalidate();
	  paintColor = Color.parseColor(newColor);
	  drawPaint.setColor(paintColor);
	}
	
	
	public void setBrushSize(float newSize)
	{//update size
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
			    newSize, getResources().getDisplayMetrics());
			brushSize=pixelAmount;
			drawPaint.setStrokeWidth(brushSize);
    }
	
	
	public void setLastBrushSize(float lastSize){
	    lastBrushSize=lastSize;
	}
	public float getLastBrushSize(){
	    return lastBrushSize;
	}
	
	public void setErase(boolean isErase)
	{//set erase true or false  
	 erase=isErase;
	 if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	 else drawPaint.setXfermode(null);
    }
	
	
	
	public void startNew()
	{
	    drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
	    invalidate();
	}
	
	
	public int getPaintAlpha(){
	    return Math.round((float)paintAlpha/255*100);
	}
	
	
	public void setPaintAlpha(int newAlpha){
	    paintAlpha=Math.round((float)newAlpha/100*255);
	    drawPaint.setColor(paintColor);
	    drawPaint.setAlpha(paintAlpha);
	}
	
	
	
}
