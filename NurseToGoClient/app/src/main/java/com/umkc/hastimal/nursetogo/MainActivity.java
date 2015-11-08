package com.umkc.hastimal.nursetogo;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//Main activity
//adding for Spark

public class MainActivity extends Activity {

    Intent intent = null;
    Intent intent1 = null;
    Context context = null;


    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear,buttonToSpark;
    //for spark
    //for direct text

    /**
     * public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
     StrictMode.setThreadPolicy(policy);
     setContentView(R.layout.main);*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        context = this;
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        //for Spark initialization
        //For direct local
        buttonToSpark = (Button)findViewById(R.id.buttonToSpark);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonToSpark.setOnClickListener(buttonToSparkOnClickListener);
        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
    }


    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {

                    if (editTextAddress != null
                            && !editTextAddress.getText().toString().isEmpty()) {
                        intent = new Intent(context, SecondActivity.class);
                        intent.putExtra("ip", editTextAddress.getText().toString());
                        Log.d("editTextAddress", editTextAddress.getText().toString() );
                        startActivity(intent);
                    }
                    else
                    {
                        textResponse.setText("IP Address of iOS field is empty");
                    }
                }};
    OnClickListener buttonToSparkOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, ThirdActivity.class);
                    startActivity(i);
                }
            };


}