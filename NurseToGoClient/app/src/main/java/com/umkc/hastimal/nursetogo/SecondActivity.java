package com.umkc.hastimal.nursetogo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
public class SecondActivity extends Activity {

    static int checkUpdate = 0;
    static String resultGesture = "";
    Intent intent = null;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    String ip = null;
    MyClientTask connectServer = null;
    Context context = null;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        intent = getIntent();
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
//	        getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        Log.d("SecondActivity", "Before IP");
        ip = intent.getStringExtra("ip");
        Log.d("SecondActivity", "After IP");
        connectServer = new MyClientTask(ip, 1234);
        Log.d("Server IP", ip);
        connectServer.execute();

    }

    /** socket logic **/
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            OutputStream outputStream;
            Socket socket = null;

            try {
                Log.d("Creating Socket", dstAddress);
                socket = new Socket(dstAddress, dstPort);
                Log.d("MyClienet Task", "Destination Address : " + dstAddress);
                Log.d("MyClient Task", "Destination Port : " + dstPort + "");
                outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                while (!socket.isClosed()) {
                    if (SecondActivity.checkUpdate == 1) {
                        Log.d("Gesture Sent", SecondActivity.resultGesture);
                        System.out.println("Gesture Sent : "
                                + SecondActivity.resultGesture);
                        printStream.print(SecondActivity.resultGesture);
                        // printStream.close();
                        SecondActivity.checkUpdate = 0;
                        SecondActivity.resultGesture = "";
                    }

                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                Log.d("ConectServer", "Unknown Host");
					/*AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
					String dispText = "IP Address is incorrect";
					dlgAlert.setMessage(dispText);
					dlgAlert.setTitle("Network Error");
					dlgAlert.setPositiveButton("OK", null);
					dlgAlert.setCancelable(true);
					dlgAlert.create().show();
					e.printStackTrace();
					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
					*/
                Log.d("ConnectService", e.toString());
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                Log.d("CoonectServer", "IO Excpetion");
                // TODO Auto-generated catch block
					/*Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);*/

                Log.d("ConnectService", e.toString());
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Log.d("ConnectServer", "In Final Client");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("ConnectServer", "Socket closed");
            super.onPostExecute(result);
        }

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    SecondActivity.resultGesture = result.get(0);
                    SecondActivity.checkUpdate = 1;
                }
                break;
            }

        }
    }

}
