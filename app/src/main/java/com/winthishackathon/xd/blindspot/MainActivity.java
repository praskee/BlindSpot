package com.winthishackathon.xd.blindspot;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.fragments.sdk.map.IndoorwayMapFragment;
import com.winthishackathon.xd.blindspot.indoorwayMapPackage.IndoorwayMapActivity;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.SpeechRecognizer.createSpeechRecognizer;

public class MainActivity extends AppCompatActivity {

    private TextView txtSpeech;
    private ImageButton btnSpeech;
    private SpeechRecognizer sr;
    private final int REQ_CODE_SPEECH_INPUT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeech = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeech = (ImageButton) findViewById(R.id.btnSpeak);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new MainActivity.listener());

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

        //Button speakButton = (Button) findViewById(R.id.btnSpeak);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new MainActivity.listener());
        btnSpeech.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return speechRecognize(view, motionEvent);
            }
        });
    }


    public boolean speechRecognize(View v, MotionEvent event) {
        SpeechRecognizer speechRecognizer = createSpeechRecognizer(MainActivity.this);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("Action", "DOWN" );

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
                sr.startListening(intent);
                return true;
            case MotionEvent.ACTION_UP:
                Log.d("Action", "UP" );
                sr.stopListening();
                return true;
        }
        return false;
    }

    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d("TAG", "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d("TAG", "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d("TAG", "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d("TAG", "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d("TAG", "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d("TAG",  "error " +  error);
            //txtSpeech.setText("error " + error);
            txtSpeech.setText(R.string.not_recognized);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d("TAG", "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d("TAG", "result " + data.get(i));
                str += data.get(i);
            }
            //txtSpeech.setText("results: "+String.valueOf(data.size()));
            txtSpeech.setText(String.valueOf(data.get(0)));
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d("TAG", "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d("TAG", "onEvent " + eventType);
        }
    }

}
