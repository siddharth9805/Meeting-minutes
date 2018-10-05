package com.sidhero.stt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class MainActivity extends AppCompatActivity {

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private SpeechToText speechService;
    private MicrophoneInputStream capture;
    private MicrophoneHelper microphoneHelper;
    public ImageButton btnRecord;
    public TextView inputMessage,print;
    public static final String tag="sidd";
    public String text;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private BottomNavigationView mBottomNav;
    private ImageButton RecordImageButton;
    int t=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord=(ImageButton)findViewById(R.id.imageButton);
        inputMessage=(TextView) findViewById(R.id.textView);
        print=(TextView)findViewById(R.id.textView2);
        microphoneHelper = new MicrophoneHelper(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mBottomNav = (BottomNavigationView)findViewById(R.id.NavBot);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        Menu menu = mBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch (item.getItemId()){
                    case R.id.logs:{
                        Intent i =new Intent(getApplicationContext(),logs.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case R.id.settings:{
                        Intent i =new Intent(getApplicationContext(),settings.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                }
                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void record(View view) {
        speechService = new SpeechToText();
        speechService.setUsernameAndPassword("abdb2ff4-f370-4551-ad70-2fa7850f503d", "8qNtvhNBDoJk");
        speechService.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");

        if(!listening) {
            inputMessage.setVisibility(View.INVISIBLE);
            inputMessage.setText("");
            text = "";
            capture = microphoneHelper.getInputStream(true);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                    } catch (Exception e) {
                        showError(e);
                    }
                }
            }).start();
            listening = true;
            //Toast.makeText(MainActivity.this,"Listening....Click to Stop", Toast.LENGTH_LONG).show();
            print.setText("LISTENING...");

        } else {
            try {
                print.setText("Press to start recording!");
//                try{
//                    Thread.sleep(2000);
//                }
//                catch(Exception e){
//
//                }
                microphoneHelper.closeInputStream();
                listening = false;
                Log.d("finishRecord",text);
                //showMicText(text);
                //Toast.makeText(MainActivity.this,"Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
                inputMessage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
        else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions() {

        return new RecognizeOptions.Builder()
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .timestamps(true)
                .inactivityTimeout(2000)
                .wordAlternativesThreshold((double) 0.9)
                //TODO: Uncomment this to enable Speaker Diarization
                .speakerLabels(true)
                .build();
    }

    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {

        @Override
        public void onTranscription(SpeechResults speechResults) {
            System.out.println(speechResults);
            //TODO: Uncomment this to enable Speaker Diarization
            SpeakerLabelsDiarization.RecoTokens recoTokens = new SpeakerLabelsDiarization.RecoTokens();
            if(speechResults.getSpeakerLabels() !=null)
            {
                recoTokens.add(speechResults);
                Log.i("SPEECHRESULTS",speechResults.getSpeakerLabels().toString());
                /*int k=t;
                t=speechResults.getSpeakerLabels().get(0).getSpeaker();
                Log.d("mihirmihir", String.valueOf(t));
                if(k!=t){
                    Log.d("mihirmihirif", String.valueOf(t));
                    //text = text.concat("\nSPEAKER "+t+": ");
                }*/

            }
            if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                text = (speechResults.getResults().get(0).getAlternatives().get(0).getTranscript());
                //Log.d(tag,text);
                //showMicText(text);
            }
            else
            {
                boolean isFound = text.indexOf("%HESITATION") !=-1? true: false;
                Log.d(tag,text);
                if(isFound){
                    text =  text.replace("%HESITATION","");
                    Log.d(tag,"if:"+isFound);

                }
                showMicText(text);
            }
        }

        @Override public void onConnected() {

        }

        @Override public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override public void onDisconnected() {
            enableMicButton();
        }

        @Override
        public void onInactivityTimeout(RuntimeException runtimeException) {

        }

        @Override
        public void onListening() {

        }

        @Override
        public void onTranscriptionComplete() {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                String TAG = "MainActivity";
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
            case MicrophoneHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        // if (!permissionToRecordAccepted ) finish();
    }
    protected void makeRequest(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MicrophoneHelper.REQUEST_PERMISSION);
    }

    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                inputMessage.append(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                btnRecord.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nav_side, menu);

        return super.onCreateOptionsMenu(menu);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            /*switch(item.getItemId()){
                case R.id.home:
                {
                    Intent i =new Intent(getApplicationContext(),home.class);
                    startActivity(i);
                    Log.d("home","home");
                    Toast.makeText(getApplicationContext(), "mihir", Toast.LENGTH_LONG).show();
                    break;
                }
                case R.id.record:
                {
                    Intent i =new Intent(getApplicationContext(),home.class);
                    startActivity(i);
                    Log.d("home","home");
                    Toast.makeText(getApplicationContext(), "mihir", Toast.LENGTH_LONG).show();
                    break;
                }

            }*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
