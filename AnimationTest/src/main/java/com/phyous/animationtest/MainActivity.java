package com.phyous.animationtest;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private final String TAG = MainActivity.class.getName();

    public static final int DELAY = 500;
    public static final int PERIOD = 2000;
    public static final String ANIMATED_VIEW_ID = "ANIMATED_VIEW_ID";
    public static final String ANIMATION_ID = "ANIMATION_ID";

    private ArrayList<String> mAnimationArray;
    private ArrayList<String> mImageArray;
    private Timer mTimer;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIndex = 0;

        // Set up animation selection through animation_selection_spinner
        mAnimationArray = getResourceNameArray(R.anim.class);
        Spinner animationSpinner = (Spinner) findViewById(R.id.animation_selection_spinner);
        animationSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mAnimationArray));
        animationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mTimer != null) mTimer.cancel();

                int animationId = getResidFromStringClass(mAnimationArray.get(position), R.anim.class, R.anim.rotate_picture);
                mTimer = new Timer();
                AnimationTimer animationTimer = new AnimationTimer(R.id.animation_image, animationId, new AnimationHandler());
                mTimer.schedule(animationTimer, DELAY, PERIOD);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set up image selector
        mImageArray = new ArrayList<String>();
        mImageArray.add("bell");
        mImageArray.add("star");
        Spinner imageSpinner = (Spinner) findViewById(R.id.image_selection_spinner);
        imageSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mImageArray));
        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView image = (ImageView) findViewById(R.id.animation_image);
                int drawableId = getResidFromStringClass(mImageArray.get(position), R.drawable.class, R.drawable.bell);
                image.setImageResource(drawableId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    /**
     * Use reflection to figure out the list of all available resources for a given class
     * @return Strings representing resources in a resource class
     */
    private ArrayList<String> getResourceNameArray(Class c) {
        ArrayList<String> resources = new ArrayList<String>();
        for (Field methodName : c.getFields()) {
            resources.add(methodName.getName());
        }
        return resources;
    }

    /**
     * Loads a resource from a class by string name
     * @param str Resource string name to laod
     * @param c class to load from
     * @param defaultResource default resource to load if reflection bombs
     * @return resource id
     */
    private int getResidFromStringClass(String str, Class c, int defaultResource) {
        int animationId = defaultResource;
        try {
            Field f = c.getField(str);
            animationId = (Integer) f.get(c);
        } catch (NoSuchFieldException e) {
            if(BuildConfig.DEBUG) {Log.e(TAG, "Failed to find resource: " + str);}
        } catch (IllegalAccessException e) {
            if(BuildConfig.DEBUG) {Log.e(TAG, "Something went wrong when accessing resource: ");}
            e.printStackTrace();
        }
        return animationId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class AnimationTimer extends TimerTask {
        private final int mViewId;
        private final int mAnimId;
        private AnimationHandler mHandler;

        public AnimationTimer(int view, int anim, AnimationHandler handler) {
            mViewId = view;
            mAnimId = anim;
            mHandler = handler;
        }

        @Override
        public void run() {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt(ANIMATED_VIEW_ID, mViewId);
            bundle.putInt(ANIMATION_ID, mAnimId);
            message.setData(bundle);
            mHandler.sendMessage(message);
            mIndex++;
        }

        @Override
        public boolean cancel() {
            mIndex = 0;
            return super.cancel();
        }
    }

    private class AnimationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            final ImageView splash = (ImageView) findViewById(bundle.getInt(ANIMATED_VIEW_ID));
            final int animationId = bundle.getInt(ANIMATION_ID);
            Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), animationId);
            splash.startAnimation(rotate);

            if (BuildConfig.DEBUG) { Log.d(TAG, "Animation iteration: " + mIndex);}
        }
    }

}
