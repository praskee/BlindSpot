package com.winthishackathon.xd.blindspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.indoorway.android.common.sdk.IndoorwaySdk;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init application context on each Application start
        IndoorwaySdk.initContext(this);
        IndoorwaySdk.configure("41bf034f-a70d-45e9-bb1e-97a72f398d0f");
    }
}
