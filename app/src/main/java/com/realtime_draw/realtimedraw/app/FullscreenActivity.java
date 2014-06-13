package com.realtime_draw.realtimedraw.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.realtime_draw.realtimedraw.app.filesys.DrawingAction;
import com.realtime_draw.realtimedraw.app.filesys.DrawingRecorder;
import com.realtime_draw.realtimedraw.app.filesys.DrawingToolBrush;
import com.realtime_draw.realtimedraw.app.util.Desen;
import com.realtime_draw.realtimedraw.app.util.deseneDataSourceClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;


public class FullscreenActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "com.realtime_draw.realtimedraw";
    private ImageButton currPaint, drawBtn, clearBtn, opacityBtn;
    private final AutobahnConnection mConnection = new AutobahnConnection();
    private int state = -1;
    private MainFragment mainFragment;
    private deseneDataSourceClass datasource;
    private EditText txtNume;
    private final String wsuri = "ws://86.127.137.166:2014/pubsub";
    private int userid = 0;
    private String fbAccessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        setupWAMP();
        state = 0;

        datasource = new deseneDataSourceClass(this);
        datasource.open();
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

        printKeyHash();
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.realtime_draw.realtimedraw.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateConnectionStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
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
                setContentView(R.layout.home);
                state = 0;
                break;
            /*
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
                setContentView(R.layout.home);
                state = 0;
                break;
                */
            case 5:
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.save_dialog, null);
                final EditText editText = (EditText) view.findViewById(R.id.editText);
                final DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);
                drawingView.pauseRecording();
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_menu_save)
                        .setTitle("Save as ...")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .setNegativeButton("Continue drawing", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                drawingView.resumeRecording();
                            }
                        })
                        .setNeutralButton("Don't save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setContentView(R.layout.home);
                                state = 0;
                            }
                        })
                        .setView(view)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nume = editText.getText().toString();
                        if (nume.equals("")) {
                            showToast("Please enter a name");
                            return;
                        }
                        if (datasource.existaDesen(nume)) {
                            showToast("Name already exists");
                            return;
                        }
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
                        saveDesen(nume);
                        setContentView(R.layout.home);
                        state = 0;
                        dialog.dismiss();
                    }
                });
                break;
            case 6:
                state = 0;
                setContentView(R.layout.home);
                updateConnectionStatus();
        }
    }

    private void setupWAMP() {
        synchronized (mConnection) {
            Log.d("wamp", "connecting to " + wsuri + "...");
            mConnection.connect(wsuri, new Autobahn.SessionHandler() {
                @Override
                public void onOpen() {
                    Log.d("wamp", "connected to " + wsuri);
                    updateConnectionStatus();
                    login();
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("wamp", "disconnected from " + wsuri);
                    updateConnectionStatus();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Log.i("wamp", "reconnecting...");
                                    setupWAMP();
                                }
                            },
                            3000
                    );
                }
            });
        }
    }

    private void login() {
        mConnection.call("auth", int[].class, new Autobahn.CallHandler() {
            @Override
            public void onResult(Object o) {
                userid = ((int[]) o)[0];
                Log.d("wamp", "logged in with userid " + userid);
            }

            @Override
            public void onError(String s, String s2) {
                Log.d("wamp", "error:" + s + ":" + s2);
            }
        }, fbAccessToken);
    }

    public void setFBAccessToken(String facebookAccessToken) {
        fbAccessToken = facebookAccessToken;
//        WAMP_auth();
    }

    protected void updateConnectionStatus() {
        // TODO: first call does not change background color of connStat
        synchronized (mConnection) {
            Log.d("ceva", "thread "+Thread.currentThread().toString());
            final Button btn_rfr = (Button) findViewById(R.id.connStat);
            Log.d("ceva", (btn_rfr == null) + " " + mConnection.isConnected());
            if (btn_rfr == null) {
                return;
            }
            int color;
            if (mConnection.isConnected())
                color = 0xFF00FF00;
            else
                color = 0xFFFF0000;
            btn_rfr.setBackgroundColor(color);
            btn_rfr.invalidate();
            Log.d("ceva", "setbackgroundcolor " + (color == 0xFFFF0000 ? "red" : "green"));
        }
    }

    public void menu_refresh(View view) {
//        showToast("refreshing...");
        updateConnectionStatus();
    }

    public void menu_gallery(View view) {
        setContentView(R.layout.see_yours);
        state = 6;
/*
        final List<Desen> values = datasource.getAllDesene();


        ArrayAdapter<Desen> adapter;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, values);

        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Desen care = values.get(position);
                long nr = care.getId();

                String filePath = "" + nr;
                try {
                    FileInputStream input = openFileInput(filePath);
                    WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
                    watchingView.play(input);
                    state = 1;
                    setContentView(R.layout.watching_view);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
*/

    }

    public void menu_draw(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE://Yes
                        //prepare stream
                        break;
                    case DialogInterface.BUTTON_NEUTRAL://No
                        /*
                        setContentView(R.layout.loading);
                        (new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                DrawingView drawingView = (DrawingView) findViewById(R.id.drawing_view);

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                setContentView(R.layout.drawing_view);
                            }
                        }).execute();
                        */
                        state = 5;
                        setContentView(R.layout.drawing_view);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE://Cancel
                        break;

                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stream realtime?").setPositiveButton("Yes", dialogClickListener)
                .setNeutralButton("No", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();

/*
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
        */
    }

    public deseneDataSourceClass getDatasource() {
        return datasource;
    }

    public void saveDesen(String nume) {
        Desen desen = datasource.createDesen(nume, "0", "0");
        try {
            File file = new File("abc.rec");
            file.renameTo(new File(String.valueOf(desen.getId())));
            /*
            FileOutputStream fileOutputStream = openFileOutput(String.valueOf(desen.getId()), Context.MODE_PRIVATE);
            FileInputStream fileInputStream = openFileInput("abc.rec");

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = fileInputStream.read(buffer)) > 0) {

                fileOutputStream.write(buffer, 0, length);

            }

            fileInputStream.close();
            fileOutputStream.close();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playDesen(long id) {
        String filePath = "" + id;
        try {
            state = 1;
            setContentView(R.layout.watching_view);
            FileInputStream input = openFileInput(filePath);
            WatchingView watchingView = (WatchingView) findViewById(R.id.watching_view);
            watchingView.play(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isWAMPConnected() {
        Log.d("altcv", "" + (mConnection == null));
        if (mConnection == null)
            return false;
        return mConnection.isConnected();
    }






























    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

        //get token ID!!!!
        if (Session.getActiveSession().isOpened()) {
            // Request user data and show the results
            Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // TODO Auto-generated method stub
                    if (user != null) {
                        // Display the parsed user info
                        Log.v(TAG, "Response : " + response);
                        Log.v(TAG, "UserID : " + user.getId());
                        Log.v(TAG, "User FirstName : " + user.getFirstName());
                    }
                }
            });
        }
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

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(FullscreenActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
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

    public void save1(View view) throws IOException {
        Desen desen = null;
        txtNume = (EditText) findViewById(R.id.txtname);

        desen = datasource.createDesen(txtNume.getText().toString(), "0", "0");


        try {
            FileOutputStream fileOutputStream = openFileOutput(String.valueOf(desen.getId()), Context.MODE_PRIVATE);
            FileInputStream fileInputStream = openFileInput("abc.rec");

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = fileInputStream.read(buffer)) > 0) {

                fileOutputStream.write(buffer, 0, length);

            }

            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        setContentView(R.layout.home);
        state = 0;

    }

    public void save2(View view) throws IOException {
        Desen desen = null;
        txtNume = (EditText) findViewById(R.id.txtname);

        desen = datasource.createDesen(txtNume.getText().toString(), "1", "0");


        try {
            FileOutputStream fileOutputStream = openFileOutput(String.valueOf(desen.getId()), Context.MODE_PRIVATE);
            FileInputStream fileInputStream = openFileInput("abc.rec");

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = fileInputStream.read(buffer)) > 0) {

                fileOutputStream.write(buffer, 0, length);

            }

            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        setContentView(R.layout.home);
        state = 0;

    }

    public void save3(View view) {
        setContentView(R.layout.home);
        state = 0;
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
            setContentView(R.layout.home);
            state = 0;
            return;
        }
        watchingView.pause();
    }

    public void new_draw(View view) {
        setContentView(R.layout.new_draw);
        state = 2;
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

    public void togglePlayButton(final Drawable drawable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageButton) findViewById(R.id.playButton)).setImageDrawable(drawable);
            }
        });
    }

}


