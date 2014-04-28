package com.realtime_draw.realtimedraw.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.realtime_draw.realtimedraw.app.filesys.DrawingAction;
import com.realtime_draw.realtimedraw.app.filesys.DrawingActionUseCoord;
import com.realtime_draw.realtimedraw.app.filesys.DrawingDecoder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingEncoder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingFrameGroup;
import com.realtime_draw.realtimedraw.app.filesys.DrawingPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class FullscreenActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "com.realtime_draw.realtimedraw";
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, clearBtn, opacityBtn;
    private Button backBtn;
    private Button start_drawing;
    private float smallBrush, mediumBrush, largeBrush;
    private final WebSocketConnection mConnection = new WebSocketConnection();

    private void testDrawing() {
        try {
            Thread.sleep(1000);
            System.out.println("Testing...");
            Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream enc_out = new ByteArrayOutputStream();
            DrawingEncoder encoder = new DrawingEncoder(enc_out, bitmap);
            System.out.println("Starting encoder...");
            encoder.start();
            long start = System.nanoTime();

            for (short j = 0; j < 600; ++j) {
                for (short i = 0; i < 100; ++i) {
                    //DrawingAction action = new DrawingActionUseCoord(i, j);
                    //encoder.queueAction(j * 1000 + i, action);
                }
            }
            encoder.queueEOS();

            encoder.join();
            long end = System.nanoTime();
            System.out.println("Finished encoding witihin " + ((end - start) / 1000000) + " milliseconds");
            System.out.println("Output size is " + enc_out.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testws() {
        final String wsuri = "ws://192.168.1.132:9000";

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage("Hello, world!");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.butoane);


        final Button btnDrawNow = (Button) findViewById(R.id.draw_now);
        btnDrawNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                draw_now();
            }
        });

        final Button btnWatch = (Button) findViewById(R.id.watch);
        btnWatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                watch();
            }
        });

        final Button btnGalery = (Button) findViewById(R.id.galery);
        btnWatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                galery();
            }
        });

    }

    public void galery() {
        //setContentView();
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(FullscreenActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void watch(/*Bundle savedInstanceState*/) {
        //super.onCreate(savedInstanceState);


        //get drawing view
        setContentView(R.layout.watch);
        drawView = (DrawingView) findViewById(R.id.drawing);
        drawView.setActivity(this);

        final Button btnback = (Button) findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                drawView.stop();
                onCreate(Bundle.EMPTY);
            }
        });

        try {
            /*
            FileInputStream in = openFileInput("abc.rec");
            while(in.available()>0)
                System.out.print(in.read());
            in.close();
*/
            FileInputStream input = openFileInput("abc.rec");
            drawView.play(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void draw_now(/*Bundle savedInstanceState*/) {
        //super.onCreate(savedInstanceState);


        //get drawing view
        drawView = (DrawingView) findViewById(R.id.drawing);


        setContentView(R.layout.activity_fullscreen);
        //get drawing view
        drawView = (DrawingView) findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        clearBtn = (ImageButton) findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(this);
        backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        opacityBtn = (ImageButton) findViewById(R.id.opacity_btn);
        opacityBtn.setOnClickListener(this);

        drawView.readonly = false;
        drawView.isRecording = true;
    }

    public void paintClicked(View view) {
        drawView.setErase(false);
        drawView.setPaintAlpha(100);
        drawView.setBrushSize(drawView.getLastBrushSize());
        //use chosen color
        if (view != currPaint) {//update color
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {//respond to clicks

        if (view.getId() == R.id.draw_btn) {//draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if (view.getId() == R.id.erase_btn) {//switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        } else if (view.getId() == R.id.clear_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Clear screen");
            newDialog.setMessage("This will paint white to all the screen");
            newDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.clearScreen();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if (view.getId() == R.id.back_btn) { //save drawing

            //save drawing
            try {
                FileOutputStream out = openFileOutput("abc.rec", MODE_PRIVATE);

                out.write(drawView.stopRecording());
                out.flush();
                out.close();
                drawView.destroyDrawingCache();
                Toast.makeText(getApplicationContext(), "Drawing saved to Gallery.", Toast.LENGTH_SHORT).show();
                drawView.readonly = true;
            } catch (Throwable e) {
                Toast.makeText(getApplicationContext(), "Recording could not be saved!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            onCreate(Bundle.EMPTY);
        } else if (view.getId() == R.id.opacity_btn) {
            //launch opacity chooser
            final Dialog seekDialog = new Dialog(this);
            seekDialog.setTitle("Opacity level:");
            seekDialog.setContentView(R.layout.opacity_chooser);
            final TextView seekTxt = (TextView) seekDialog.findViewById(R.id.opq_txt);
            final SeekBar seekOpq = (SeekBar) seekDialog.findViewById(R.id.opacity_seek);
            seekOpq.setMax(100);
            int currLevel = drawView.getPaintAlpha();
            seekTxt.setText(currLevel + "%");
            seekOpq.setProgress(currLevel);
            seekOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekTxt.setText(Integer.toString(progress) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            Button opqBtn = (Button) seekDialog.findViewById(R.id.opq_ok);
            opqBtn.setOnClickListener(new View.OnClickListener() {
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
