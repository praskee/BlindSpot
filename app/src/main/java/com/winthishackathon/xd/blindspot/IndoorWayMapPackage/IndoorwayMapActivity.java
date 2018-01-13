package com.winthishackathon.xd.blindspot.IndoorWayMapPackage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.indoorway.android.fragments.sdk.map.MapFragment;

/**
 * Created by Tomasz on 13/01/2018.
 */

public class IndoorwayMapActivity extends Activity implements IndoorwayMapFragment.OnMapFragmentReadyListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapFragmentReady(MapFragment mapFragment) {

    }
}
