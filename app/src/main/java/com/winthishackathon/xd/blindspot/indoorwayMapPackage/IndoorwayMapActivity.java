package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayNode;
import com.indoorway.android.common.sdk.model.IndoorwayPosition;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;
import com.indoorway.android.location.sdk.IndoorwayLocationSdk;
import com.indoorway.android.map.sdk.view.MapView;
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;
import com.winthishackathon.xd.blindspot.R;


import java.util.HashMap;
import java.util.List;

/**
 */

public class IndoorwayMapActivity extends AppCompatActivity implements IndoorwayMapFragment.OnMapFragmentReadyListener {
    private String localizationName = null;
    private double pikAngle = 0;

    int tapCounter = 0;
    int resultTapCounter = 0;
    Handler h = new Handler();
    int delay = 1000; //1 seconds
    Runnable runnable;
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoorway_map);
        // get extras (where to go)
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        tapCounter = 0;
        resultTapCounter = 0;
        super.onPause();
    }

    @Override
    protected void onResume() {
    //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                tapCounter = 0;
                resultTapCounter = 0;
                Log.i("Timer","Counters got cleared");
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }
    @Override
    public void onMapFragmentReady(final MapFragment mapFragment) {
        final MapView mv = mapFragment.getMapView();

        IndoorwayMapFragment.Config config = new IndoorwayMapFragment.Config();
        config.setLocationButtonVisible(false);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            localizationName = extras.getString("ROOM_PASSED");
            Log.i("ROOMPASSED",localizationName);
        }
        //TODO: ZMIENIC PIETRO
        mv.load("CScrSxCVhQg","3-_M01M3r5w");
        mv.getTouch().setOnClickListener(new Action1<Coordinates>() {
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
                    final AlertDialog dialog = mBuilder.create();

                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    dialog.show();
                }
            }
        });
        mv.setOnMapLoadCompletedListener(new Action1<IndoorwayMap>() {
            boolean flaga = false;
            String firstMapUUID = "";
            @Override
            public void onAction(final IndoorwayMap indoorwayMap) {
                if(flaga){
                    if(!firstMapUUID.equals(indoorwayMap.getMapUuid())) return;
                }
                else{
                    firstMapUUID = indoorwayMap.getMapUuid();
                    flaga = true;
                }

                floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
                floatingActionButton.performClick();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Coordinates coordinates = null;
                        try {
                            coordinates = IndoorSDKUtils.getPositionFromObjectName(localizationName, indoorwayMap);
                            Log.d("indoorway",coordinates.toString() + localizationName);
                        } catch (Exception e) {
                            Log.e("indoorway", "Couldn't find specified object " + localizationName);
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                        final List<IndoorwayNode> paths = indoorwayMap.getPaths();
                        for(IndoorwayNode n:paths) {
                            for(Long l:n.getNeighbours()) {
                                if(l == n.getId()) {
                                    Log.d("indoorway","KURWAAAAAA "+n.toString());
                                }
                            }
                        }
                        HashMap adjacencyMap = IndoorSDKUtils.getMapPaths(paths);

                        Long destinationId = IndoorSDKUtils.getNearestToCoordinates(adjacencyMap, coordinates);
                        HashMap<FromToContainer, Double> dist = Pathfinding.Dijkstra(adjacencyMap, destinationId);

                        MarkersLayer myLayer = mapFragment.getMapView().getMarker().addLayer(10.0F);

                        Action1<Float> listener = new Action1<Float>() {
                            @Override
                            public void onAction(Float v) {
                                double angles = paths.get(0).getCoordinates().getAngleTo(paths.get(1).getCoordinates());
                                double out = angles - v;
                                if (out < 0) out += 360;
                                out -= 90;
                                if (out < 0) out += 360;
                                setPikAngle(out);
                                Log.d("indoorway", Float.toString(v));
                            }
                        };

                        IndoorwayLocationSdk.instance().direction().onHeadingChange().register(listener);
                        while (true) {
                            IndoorwayPosition currentPosition = mapFragment.getLastKnownPosition();
                            if(currentPosition == null) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                continue;
                            }
                            Coordinates currentCoordinates = currentPosition.getCoordinates();

                            Long currentId = IndoorSDKUtils.getNearestToCoordinates(adjacencyMap, currentCoordinates);
                            List<MapNode> path = Pathfinding.getPathFromTo(adjacencyMap, dist, destinationId, currentId);




                            for (int i = 0; i < path.size(); i++) {
                                Coordinates coord = new Coordinates(path.get(i).Lat, path.get(i).Lon);
                                myLayer.add(new DrawableCircle(Integer.toString(i), 0.4f,
                                        Color.GREEN, Color.BLACK, 0.0f, coord));
                            }
                            float multiplier = 1.0f;
                            double myPikAngle = pikAngle;
                            if(pikAngle > 180) {
                                multiplier = 0.8f;
                                if(pikAngle > 270) {
                                    myPikAngle = 360 - pikAngle;
                                } else {
                                    myPikAngle = 90 - (pikAngle-90);
                                }
                            }
                            float leftVolume = 1 - (float)(myPikAngle/180);
                            float rightVolume = (float)myPikAngle / 180;
                            //playSound(currentCoordinates, path);
                            MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pikpik);
                            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mPlayer.setLooping(false);
                            mPlayer.setVolume(leftVolume * multiplier,rightVolume * multiplier);
                            mPlayer.start();
                            if (path.size() < 2) {
                                break;
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                Log.e("indoorway", "Error while sleeping " + e.toString());
                                break;
                            }
                            for (int i = 0; i < path.size(); i++)
                                myLayer.remove(Integer.toString(i));
                            }
                        }

                }).start();
            }
        });

    }
    private void setPikAngle(double out){
        pikAngle = out;
    }

}
