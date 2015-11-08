package com.umkc.hastimal.nursetogo;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    Context context = null;
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

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
                        textResponse.setText("IP Address field is empty");
                    }
                }};
}