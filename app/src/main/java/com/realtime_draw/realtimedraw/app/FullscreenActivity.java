package com.realtime_draw.realtimedraw.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.Signature;

import com.facebook.Session;
import com.realtime_draw.realtimedraw.app.filesys.DrawingAction;
import com.realtime_draw.realtimedraw.app.filesys.DrawingRecorder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingToolBrush;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;


public class FullscreenActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "com.realtime_draw.realtimedraw";
    private ImageButton currPaint, drawBtn, clearBtn, opacityBtn;
    private final AutobahnConnection mConnection = new AutobahnConnection();
    private int state = -1;
    private MainFragment mainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        state = 0;
        setupWAMP();
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }
    private void setupWAMP() {
        final String wsuri = "ws://192.168.1.3:8080";
        mConnection.connect(wsuri, new Autobahn.SessionHandler() {
            @Override
            public void onOpen() {
                updateConnectionStatus();
            }

            @Override
            public void onClose(int code, String reason) {
                updateConnectionStatus();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);


    }


    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(FullscreenActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void gomenu(View view) {
        setContentView(R.layout.secondh);
    }


    public void watch(View view) {
        setContentView(R.layout.watching_view);
        state = 1;
        try {
            FileInputStream input = openFileInput("abc.rec");
            WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
            watchingView.play(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(View view) {
        WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
        if (watchingView.isFinished()) {
            watchingView.stop();
            setContentView(R.layout.secondh);
            state = 0;
            return;
        }
        watchingView.pause();
    }

    public void draw_now(View view) {
        setContentView(R.layout.drawing_view);
        state = 2;
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors_row2);
        currPaint = (ImageButton) paintLayout.getChildAt(3);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);
        drawingView.setBrushSize(DrawingToolBrush.SMALL);
        clearBtn = (ImageButton) findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(this);
        opacityBtn = (ImageButton) findViewById(R.id.opacity_btn);
        opacityBtn.setOnClickListener(this);
        drawingView.isRecording = true;
    }

    public void draw_and_send(View view) {
        if (!mConnection.isConnected()) {
            showToast("not connected");
            return;
        }
        draw_now(view);
        state = 3;
        DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);
        drawingView.setOnDraw(new DrawingRecorder.onAddListener() {
            @Override
            public void onAdd(int timeIndex, DrawingAction action) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    action.encode(baos);
//                    System.out.println("send: "+action);
                    MySend mySend = new MySend();
                    mySend.timeIndex = timeIndex;
                    mySend.action = baos.toByteArray();
                    mConnection.publish("test1", mySend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void receive_and_play(View view) {
        if (!mConnection.isConnected()) {
            showToast("not connected");
            return;
        }
        setContentView(R.layout.watching_view);
        state = 4;
        WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
        watchingView.passivePlay();
        mConnection.subscribe("test1",
                MySend.class,
                new Autobahn.EventHandler() {
                    @Override
                    public void onEvent(String topic, final Object event) {
                        (new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {

                                    MySend evt = (MySend) event;
                                    ByteArrayInputStream bais = new ByteArrayInputStream(evt.action);
                                    DrawingAction action = DrawingAction.decode(bais);
//                                    System.out.println("received: "+action);
                                    WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
                                    watchingView.playAction(action);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, "test1.onEvent")).start();
                    }
                }
        );
    }

    private static class MySend {
        public int timeIndex;
        public byte[] action;
    }

    public void paintClicked(View view) {
        DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);
        //use chosen color
        if (view != currPaint) {//update color
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawingView.setColor(color);
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
        final DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);
        if (view.getId() == R.id.draw_btn) {//draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setBrushSize(DrawingToolBrush.SMALL);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setBrushSize(DrawingToolBrush.MEDIUM);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawingView.setBrushSize(DrawingToolBrush.LARGE);
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
                    drawingView.clearScreen();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if (view.getId() == R.id.opacity_btn) {
            //launch opacity chooser
            final Dialog seekDialog = new Dialog(this);
            seekDialog.setTitle("Opacity level:");
            seekDialog.setContentView(R.layout.opacity_chooser);
            final TextView seekTxt = (TextView) seekDialog.findViewById(R.id.opq_txt);
            final SeekBar seekOpq = (SeekBar) seekDialog.findViewById(R.id.opacity_seek);
            seekOpq.setMax(100);
            int currLevel = drawingView.getPaintAlpha();
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
                    drawingView.setPaintAlpha(seekOpq.getProgress());
                    seekDialog.dismiss();
                }
            });
            seekDialog.show();

        }

    }

    public void togglePlayButton(final Drawable drawable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageButton) findViewById(R.id.playButton)).setImageDrawable(drawable);
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (state) {
            default:
            case 0:
                super.onBackPressed();
                break;
            case 4:
                mConnection.unsubscribe("test1");
            case 1:
                WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
                watchingView.stop();
                setContentView(R.layout.secondh);
                state = 0;
                break;
            case 3:
            case 2:
                try {
                    FileOutputStream out = openFileOutput("abc.rec", MODE_PRIVATE);
                    final DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);
                    out.write(drawingView.stopRecording());
                    out.flush();
                    out.close();
                    drawingView.destroyDrawingCache();
                    showToast("Drawing saved to Gallery.");
                } catch (Throwable e) {
                    showToast("Recording could not be saved!");
                    e.printStackTrace();
                }
                setContentView(R.layout.secondh);
                state = 0;
                break;
        }
    }

    synchronized public void WAMP_auth(String facebookAccessToken){
        if(!mConnection.isConnected()){
            showToast("not connected");
            return;
        }

        //TODO: error on startup
        /*
        mConnection.call("auth", int.class, new Autobahn.CallHandler() {
            @Override
            public void onResult(Object o) {
                int userId = (Integer)o;
                Log.d(TAG, "auth result:"+userId);
            }

            @Override
            public void onError(String s, String s2) {
                Log.d(TAG, "auth result:"+s+":"+s2);
            }
        }, facebookAccessToken);
        */
    }

    protected void updateConnectionStatus(){
        ImageView iv = (ImageView)findViewById(R.id.connectionStatus);
        if(mConnection.isConnected()){
            iv.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }else{
            iv.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }
}


