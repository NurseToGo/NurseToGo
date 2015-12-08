package com.umkc.hastimal.nursetogo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;


public class ThirdActivity extends Activity {

    EditText textOut;
    TextView textIn;
    Button buttonClear2;
    // for camera
//    private static final int REQUEST_CODE = 1;
//    private Bitmap bitmap;
//    private ImageView imageView;
//    Button cameraButton;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        textOut = (EditText)findViewById(R.id.textout);
        Button buttonSend = (Button)findViewById(R.id.send);
        textIn = (TextView)findViewById(R.id.textin);
        buttonSend.setOnClickListener(buttonSendOnClickListener);
        buttonClear2 =(Button)findViewById(R.id.buttonClear2);
        buttonClear2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textOut.setText("");
            }});
        //camera
//        imageView = (ImageView) findViewById(R.id.result);
//        Button buttonCamera = (Button)findViewById(R.id.btnCapture);
//        buttonCamera.setOnClickListener(buttonCameraSendOnClickListener);

    }
    //camera
//    View.OnClickListener buttonCameraSendOnClickListener =
//            new View.OnClickListener(){
//
//                @Override
//                public void onClick(View arg0) {
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    startActivityForResult(intent, REQUEST_CODE);
//                }};



    Button.OnClickListener buttonSendOnClickListener
            = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket("10.205.0.67", 4094);//10.142.0.193  Spark IP
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(textOut.getText().toString());
                textIn.setText(dataInputStream.readUTF());
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally{
                if (socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null){
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null){
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }};
}