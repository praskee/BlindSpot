package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;
import com.winthishackathon.xd.blindspot.R;

/**
 * Created by Tomasz on 13/01/2018.
 */

public class IndoorwayMapActivity extends AppCompatActivity implements IndoorwayMapFragment.OnMapFragmentReadyListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoorway_map);
    }
    
    @Override
    public void onMapFragmentReady(MapFragment mapFragment) {
        mapFragment.getMapView().load("CScrSxCVhQg","3-_M01M3r5w");
    }
}
