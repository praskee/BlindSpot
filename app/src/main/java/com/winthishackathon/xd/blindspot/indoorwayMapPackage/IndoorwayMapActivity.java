package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.indoorway.android.map.sdk.view.MapView;
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle;
import com.indoorway.android.map.sdk.view.drawable.layers.MarkersLayer;
import com.winthishackathon.xd.blindspot.MainActivity;
import com.winthishackathon.xd.blindspot.R;


import java.util.HashMap;
import java.util.List;

/**
 * Created by Tomasz on 13/01/2018.
 */

public class IndoorwayMapActivity extends AppCompatActivity implements IndoorwayMapFragment.OnMapFragmentReadyListener {
    private String localizationName = null;


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

            @Override
            public void onAction(final IndoorwayMap indoorwayMap) {
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
                            IndoorwayMapActivity.this.finish();
                        }
                        List<IndoorwayNode> paths = indoorwayMap.getPaths();
                        for(IndoorwayNode n:paths) {
                            for(Long l:n.getNeighbours()) {
                                if(l == n.getId()) {
                                    Log.d("indoorway","KURWAAAAAA "+n.toString());
                                }
                            }
                        }
                        HashMap adjacencyMap = IndoorSDKUtils.getMapPaths(paths);
                        try {
                            Long destinationId = IndoorSDKUtils.getNearestToCoordinates(adjacencyMap, coordinates);
                            HashMap<FromToContainer, Double> dist = Pathfinding.Dijkstra(adjacencyMap, destinationId);

                            MarkersLayer myLayer = mapFragment.getMapView().getMarker().addLayer(10.0F);
                            while (true) {
                                IndoorwayPosition currentPosition = mapFragment.getLastKnownPosition();
                                if(currentPosition == null) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        finish();
                                    }
                                    continue;
                                }
                                Coordinates currentCoordinates = currentPosition.getCoordinates();
                                //Coordinates currentCoordinates = null;
    //                                try {
    //                                    currentCoordinates = IndoorSDKUtils.getPositionFromObjectName("sala 216", indoorwayMap); // TODO: MOCK!!!!!
    //                                } catch (Exception e) {
    //                                    Log.e("indoorway", "MOCK POPSUL");
    //                                }

                                Long currentId = IndoorSDKUtils.getNearestToCoordinates(adjacencyMap, currentCoordinates);
                                List<MapNode> path = Pathfinding.getPathFromTo(adjacencyMap, dist, destinationId, currentId);


                                for (int i = 0; i < path.size(); i++) {
                                    Coordinates coord = new Coordinates(path.get(i).Lat, path.get(i).Lon);
                                    myLayer.add(new DrawableCircle(Integer.toString(i), 0.4f,
                                            Color.GREEN, Color.BLACK, 0.0f, coord));
                                }
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
                            } catch (Exception e){
                                Log.e("indoorway", "Couldn't find specified object");
                            }
                        }

                }).start();
            }
        });

    }

}
