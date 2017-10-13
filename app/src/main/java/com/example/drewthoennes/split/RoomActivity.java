package com.example.drewthoennes.split;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.*;

import java.util.ArrayList;

import static com.example.drewthoennes.split.R.layout.activity_room;

/**
 * Created by drewthoennes on 9/16/17.
 */

public class RoomActivity extends AppCompatActivity {

    //Button endButton;
    RelativeLayout activity_room_top;
    RelativeLayout titleBar;
    TextView timesIcon;
    TextView settingsIcon;
    com.beardedhen.androidbootstrap.BootstrapButton startVideoButton;
    TextView hostAccessCode;
    TextView countUsersText;
    Socket socket;

    String roomCode = "Default Room";
    String userId;
    Boolean host;
    String link;

    Typeface iconFont;
    FontManager fontManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_room);

        iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        fontManager.markAsIconContainer(findViewById(R.id.times_icon), iconFont);
        fontManager.markAsIconContainer(findViewById(R.id.settings_icon), iconFont);

        activity_room_top = (RelativeLayout) findViewById(R.id.activity_room_top);
        activity_room_top.setBackgroundColor(Color.parseColor("#1477d5"));

        titleBar = (RelativeLayout) findViewById(R.id.titleBar);
        titleBar.setBackgroundColor(Color.parseColor("#282828"));

        timesIcon = (TextView) findViewById(R.id.times_icon);
        timesIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                socket.close();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        settingsIcon = (TextView) findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        // Get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        final int screenHeight = displayMetrics.heightPixels;
        final int screenWidth = displayMetrics.widthPixels;
        final int xDpi = (int) displayMetrics.xdpi;
        final int yDpi = (int) displayMetrics.ydpi;

        if(getIntent().getStringExtra("roomCode").toString() != null || getIntent().getStringExtra("roomCode").toString().isEmpty()) {
            roomCode = getIntent().getStringExtra("roomCode").toString();
        }

        host = getIntent().getExtras().getBoolean("host");
        if(getIntent().getStringExtra("link") != null) {
            link = getIntent().getStringExtra("link").toString();
        }
        else {
            link = "";
        }

        try {
            socket = IO.socket("http://elnardu.me/" + roomCode);
        } catch(Exception exception) {
            Log.e("Error", exception.getMessage()); // Temporary, shoud be removed in final release
            //System.exit(0);
        }
//        socket.connect();

//        if(!link.equals("")) {
//            socket.emit("download", link);
//        }

        // Send dimensions
        JSONObject settings = new JSONObject();
        try {
            settings.put("width", screenWidth);
            settings.put("height", screenHeight);
            settings.put("xdpi", xDpi);
            settings.put("ydpi", yDpi);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        countUsersText = (TextView) findViewById(R.id.countUsersText);

//        socket.emit("join", settings);

//        socket.on("joinAccepted", new Emitter.Listener() {
//            public void call(Object... args) {
//                String data = (String) args[0];
//                userId = data;
//            }
//        }).on("prepare", new Emitter.Listener() {
//            public void call(Object... args) {
//                play();
//            }
//        }).on("usersCount", new Emitter.Listener() {
//            public void call(Object... args) {
//                final String data = (String) args[0];
//                RoomActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        countUsersText.setText(data);
//                    }
//                });
//            }
//        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
//            public void call(Object... args) {
//                Exception err = (Exception) args[0];
//            }
//        });

        hostAccessCode = (TextView) findViewById(R.id.hostAccessCode);
        hostAccessCode.setText(roomCode);

        startVideoButton = (com.beardedhen.androidbootstrap.BootstrapButton) findViewById(R.id.startVideoButton);
        startVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startVideoButton.setEnabled(false);
                if(!link.equals("")) {
//                    socket.emit("prepare", "youtube");
                }
                else {
//                    socket.emit("prepare", "demo");
                }
            }
        });

        if(!host) {
            //startVideoButton.setVisibility(View.INVISIBLE); // Uncomment this before final release
            //startVideoButton.setEnabled(false);
        }

    }

    public void play() {
        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("roomCode", roomCode);
        RoomActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                startVideoButton.setEnabled(true);
            }
        });
        startActivity(intent);

    }
}
