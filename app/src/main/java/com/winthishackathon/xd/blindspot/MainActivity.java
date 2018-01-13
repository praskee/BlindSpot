package com.winthishackathon.xd.blindspot;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.wang.avi.AVLoadingIndicatorView;
import com.winthishackathon.xd.blindspot.indoorwayMapPackage.IndoorwayMapActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.speech.SpeechRecognizer.createSpeechRecognizer;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT = 1;
    static final int MY_PERMISSIONS_REQUEST_MICROPHONE = 2;
    private static final String TAG = "MainActivity";
    private TextView txtSpeech;
    private ImageButton btnSpeech;
    private ImageButton btnConfirm;
    private SpeechRecognizer sr;
    private TextToSpeech textToSpeech;
    private final int REQ_CODE_SPEECH_INPUT = 10;
    private AVLoadingIndicatorView loadingButton;
    private boolean yesFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeech = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeech = (ImageButton) findViewById(R.id.btnSpeak);
        btnConfirm = (ImageButton) findViewById(R.id.btnConfirm);
        loadingButton = (AVLoadingIndicatorView) findViewById(R.id.speechLoader);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new MainActivity.listener());
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //kiedy nie ma bluetootha
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("You need a bluetooth module to run this application");
            builder1.setCancelable(true);


            builder1.setNeutralButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
            //TODO: dialog pop up ze apka nie dziala bez blufiuta
         else
        //jesli bluetooth jest wylaczony requestuj permissiony w runtimie
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_MICROPHONE);

        // init application context on each Application start
        IndoorwaySdk.initContext(this);
        IndoorwaySdk.configure("41bf034f-a70d-45e9-bb1e-97a72f398d0f");

        //Button speakButton = (Button) findViewById(R.id.btnSpeak);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new MainActivity.listener());
        btnSpeech.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showLoadingButton();
                return speechRecognize(view, motionEvent);
            }
        });

        //TODO: podmianka buttona + handling yes/no to new activity

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions2, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_MICROPHONE:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

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
                showMicrophoneButton();
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
            if(String.valueOf(data.get(0)).equals("tak") && yesFlag)
            {
                Log.d("STTRES","Zmien aktiwiti");
                Intent intent = new Intent(MainActivity.this,IndoorwayMapActivity.class);
                startActivity(intent);
                readString("Przechodze do nawigacji");
            }
            else {
                txtSpeech.setText(String.valueOf(data.get(0)));
                readString(getResources().getString(R.string.question) + " " + String.valueOf(data.get(0) + getResources().getString(R.string.pause)));
                languageProcessing(String.valueOf(data.get(0)));
            }

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

    private void readString(String toSpeak) {
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void languageProcessing(String input) {
        List<String> result = Arrays.asList(input.split(" "));
        for (int i = 0; i < result.size(); i++) {
            // Looking for a lift
            if(result.size() == 1 && result.get(i).equals(new String("winda")) || result.get(i).equals(new String("windy"))) {
                Log.d("Lift:", result.get(i).toString());
                break;
            } else if (result.get(i).equals(new String("sala")) || result.get(i).equals(new String("sali"))) {
                try {
                    Integer.parseInt(result.get(i + 1));
                    Log.d("Room:", result.get(i + 1).toString());
                    //TODO: Navigate to room NUMBER
                    yesFlag = true;
                        break;
                } catch (NumberFormatException ex) {
                    Log.d("Exception:", ex.toString());
                    //TODO: String is not a number
                } catch (IndexOutOfBoundsException iex){
                    Log.d("Exception:", iex.toString());
                }
            }
        }
    }

    private void showLoadingButton() {
        loadingButton.setVisibility(View.VISIBLE);
        btnSpeech.setVisibility(View.INVISIBLE);
    }

    private void showMicrophoneButton(){
        loadingButton.setVisibility(View.INVISIBLE);
        btnSpeech.setVisibility(View.VISIBLE);
    }
}
