package com.phyous.animationtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private ImageView _imagView;
    private Timer _timer;
    private int _index;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new MyHandler();
        _imagView = (ImageView) findViewById(R.id.bell_image);

        _index = 0;
        _timer = new Timer();
        _timer.schedule(new TickClass(), 500, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class TickClass extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            handler.sendEmptyMessage(_index);
            _index++;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            final ImageView splash = (ImageView) findViewById(R.id.bell_image);
            Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_picture);
            splash.startAnimation(rotate);

            Log.v("Rotating Image: ", _index + "");

        }
    }

}
