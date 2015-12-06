package Kafka.Spark.Sentimental;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by hastimal on 11/4/2015.
 */
public class ServerTestingAndroid {
    public static void main(String[] args){
        ServerSocket serverSocket = null;
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            serverSocket = new ServerSocket(4321);
            System.out.println("Listening :4321");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while(true){
            try {
                socket = serverSocket.accept();
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("ip: " + socket.getInetAddress());
                System.out.println("message: " + dataInputStream.readUTF());

                //dataOutputStream.writeUTF("Hello!..I am listening..");
                // else{
                //     dataOutputStream.writeUTF(dataInputStream.readUTF());
                /// }
                /**serverSocket.accept socket = ();
                 DataInputStream = new DataInputStream (socket.getInputStream ());
                 DataOutputStream = new DataOutputStream (socket.getOutputStream ());
                 DataStream = dataInputStream.readUTF String ();
                 System.out.println ("ip:" + socket.getInetAddress ());
                 System.out.println ("message:" + dataInputStream.readUTF ());
                 dataOutputStream.writeUTF (DataStream);
                 */

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally{
                if( socket!= null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if( dataInputStream!= null){
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if( dataOutputStream!= null){
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
