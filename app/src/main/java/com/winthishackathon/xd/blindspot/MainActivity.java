package com.winthishackathon.xd.blindspot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.winthishackathon.xd.blindspot.indoorwayMapPackage.IndoorwayMapActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton button = (ImageButton)findViewById(R.id.btnSpeak);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IndoorwayMapActivity.class);
                startActivity(intent);
            }
        });

        // init application context on each Application start
        IndoorwaySdk.initContext(this);
        IndoorwaySdk.configure("41bf034f-a70d-45e9-bb1e-97a72f398d0f");
    }
}
