package com.example.drawingfun.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;






public class MainActivity extends Activity implements OnClickListener {
	
	private DrawingView drawView;
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	private float smallBrush, mediumBrush, largeBrush;
	

	
	private ImageButton opacityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        //get drawing view
        drawView = (DrawingView)findViewById(R.id.drawing);
        
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        
        opacityBtn = (ImageButton)findViewById(R.id.opacity_btn);
    	opacityBtn.setOnClickListener(this);
        
    }


 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   
  /*  public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    */
    
    public void paintClicked(View view)
    {    drawView.setErase(false);
         drawView.setPaintAlpha(100);
         drawView.setBrushSize(drawView.getLastBrushSize());
        //use chosen color
    	if(view!=currPaint)
    	{//update color
    		ImageButton imgView = (ImageButton)view;
    		String color = view.getTag().toString();
    		drawView.setColor(color);
    		imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
    		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
    		currPaint=(ImageButton)view;
    	}
    }
    
    

    @Override
    public void onClick(View view)
    {//respond to clicks
   
     if(view.getId()==R.id.draw_btn)
        {//draw button clicked
    	 final Dialog brushDialog = new Dialog(this);
    	 brushDialog.setTitle("Brush size:");
    	 brushDialog.setContentView(R.layout.brush_chooser);
    	 ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
    	 smallBtn.setOnClickListener(new OnClickListener(){
    	     @Override
    	     public void onClick(View v) {
    	    	 drawView.setErase(false);
    	         drawView.setBrushSize(smallBrush);
    	         drawView.setLastBrushSize(smallBrush);
    	         brushDialog.dismiss();
    	     }
    	 });
    	 ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
    	 mediumBtn.setOnClickListener(new OnClickListener(){
    	     @Override
    	     public void onClick(View v) {
    	    	 drawView.setErase(false);
    	         drawView.setBrushSize(mediumBrush);
    	         drawView.setLastBrushSize(mediumBrush);
    	         brushDialog.dismiss();
    	     }
    	 });
    	  
    	 ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
    	 largeBtn.setOnClickListener(new OnClickListener(){
    	     @Override
    	     public void onClick(View v) {
    	    	 drawView.setErase(false);
    	         drawView.setBrushSize(largeBrush);
    	         drawView.setLastBrushSize(largeBrush);
    	         brushDialog.dismiss();
    	     }
    	 });
    	 
    	 brushDialog.show();
    	}
     else if(view.getId()==R.id.erase_btn)
       {//switch to erase - choose size
    	 final Dialog brushDialog = new Dialog(this);
    	 brushDialog.setTitle("Eraser size:");
    	 brushDialog.setContentView(R.layout.brush_chooser);
    	 ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
      }
      else if(view.getId()==R.id.new_btn)
        {AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                drawView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newDialog.show();
        }
     
      else if(view.getId()==R.id.save_btn)
        { //save drawing
    	  
    	//save drawing
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save drawing");
			saveDialog.setMessage("Save drawing to device Gallery?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					//save drawing
					drawView.setDrawingCacheEnabled(true);
					//attempt to save
					String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),UUID.randomUUID().toString()+".png", "drawing");
					//feedback
					if(imgSaved!=null){
						Toast savedToast = Toast.makeText(getApplicationContext(), 
								"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
						savedToast.show();
					}
					else{
						Toast unsavedToast = Toast.makeText(getApplicationContext(), 
								"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
						unsavedToast.show();
					}
					drawView.destroyDrawingCache();
				}
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
			saveDialog.show();
        }
     
      else if(view.getId()==R.id.opacity_btn)
      {
    	    //launch opacity chooser
    	  final Dialog seekDialog = new Dialog(this);
    	  seekDialog.setTitle("Opacity level:");
    	  seekDialog.setContentView(R.layout.opacity_chooser);
    	  final TextView seekTxt = (TextView)seekDialog.findViewById(R.id.opq_txt);
    	  final SeekBar seekOpq = (SeekBar)seekDialog.findViewById(R.id.opacity_seek);
    	  seekOpq.setMax(100);
    	  int currLevel = drawView.getPaintAlpha();
    	  seekTxt.setText(currLevel+"%");
    	  seekOpq.setProgress(currLevel);
    	  seekOpq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
    		    @Override
    		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    		        seekTxt.setText(Integer.toString(progress)+"%");
    		    }
    		    @Override
    		    public void onStartTrackingTouch(SeekBar seekBar) {}
    		    @Override
    		    public void onStopTrackingTouch(SeekBar seekBar) {}
    		});
    	  Button opqBtn = (Button)seekDialog.findViewById(R.id.opq_ok);
    	  opqBtn.setOnClickListener(new OnClickListener(){
    		    @Override
    		    public void onClick(View v) {
    		        drawView.setPaintAlpha(seekOpq.getProgress());
    		        seekDialog.dismiss();
    		    }
    		});
    	  seekDialog.show();
      
      }

 }
    
    
    
    

}
