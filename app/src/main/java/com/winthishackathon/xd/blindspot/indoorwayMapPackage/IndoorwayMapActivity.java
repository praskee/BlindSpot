package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;
import com.indoorway.android.map.sdk.view.MapView;
import com.winthishackathon.xd.blindspot.R;

import java.util.Collection;

/**
 * Created by Tomasz on 13/01/2018.
 */

public class IndoorwayMapActivity extends AppCompatActivity implements IndoorwayMapFragment.OnMapFragmentReadyListener {

    int tapCounter = 0;
    Handler h = new Handler();
    int delay = 1000; //1 seconds
    Runnable runnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoorway_map);
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    protected void onResume() {
    //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                tapCounter = 0;
                Log.i("Timer","Counter got cleared");
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }
    @Override
    public void onMapFragmentReady(MapFragment mapFragment) {
        mapFragment.getMapView().load("CScrSxCVhQg","3-_M01M3r5w");
        MapView mapView = mapFragment.getMapView();
        mapView.getTouch().setOnClickListener(new Action1<Coordinates>() {
            @Override
            public void onAction(Coordinates coordinates) {
                Log.i("DETEKTYW","TAP DETECTED");
                tapCounter++;
                if(tapCounter%3 == 0)
                {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(300);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(IndoorwayMapActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_exit_nav,null);
                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();

                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    dialog.show();
                }
            }
        });
    }
}
