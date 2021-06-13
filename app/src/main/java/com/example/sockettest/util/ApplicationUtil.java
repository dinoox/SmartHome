package com.example.sockettest.util;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.sockettest.MainActivity;
import com.example.sockettest.bean.ConverEnvInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ApplicationUtil extends Application {

    public static final String HOST = "noox.top";
    public static final int PORT = 9000;

    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public void init() {
            new Thread(() -> {
                if (socket == null) {
                    try {
                        socket = new Socket(HOST, PORT);
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }


    public void connectServer(MainActivity activity) {

        new Thread(() -> {
            try {
                byte[] bytes = new byte[30];

                while (true){
                    inputStream.read(bytes);

                    ConverEnvInfo cei = new ConverEnvInfo();
                    cei.setByteBuffer(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN), 0);

                    Intent intent = new Intent("dataUpdate");
                    activity.putUpdatedData(intent,cei);
                    LocalBroadcastManager.getInstance(ApplicationUtil.this).sendBroadcast(intent);

                    activity.runOnUiThread(() -> activity.renderData(cei));
                }

            } catch (Exception e) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(ApplicationUtil.this,"网络连接出错",Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    public void sendCommand(String cmd) {
        new Thread(() -> {
            try{
                byte[] cmdStruct = new byte[2];
                cmdStruct[1] = (byte) Integer.parseInt(cmd, 2);
                outputStream.write(cmdStruct);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
