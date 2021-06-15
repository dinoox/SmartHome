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

/**
 * 一个Android程序如果需要和服务器建立socket通信的话，在每个activity中都新建socket与服务器连接是非常不便的
 * 那么我们就需要在各个activity之间传递socket，这样只需要和服务器建立一次连接就可以了
 * <p>
 * Android系统会为每个程序运行时创建一个Application类的对象且仅创建一个
 * 所以Application可以说是单例 (singleton)模式的一个类.且application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期
 * 因为它是全局 的单例的，所以在不同的Activity,Service中获得的对象都是同一个对象。所以通过Application来进行一些，数据传递，数据共享等,数据缓存等操作
 */


public class ApplicationUtil extends Application {

    public static String HOST;
    public static int PORT;

    public Socket socket = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;

    //初始化与服务器建立连接
    public void ConnectServer(MainActivity activity) {

        new Thread(() -> {

            try {
                    if (socket != null)
                        socket.close();

                    socket = new Socket(HOST, PORT);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                    byte[] bytes = new byte[30];

                    while (!socket.isClosed()) {

                        inputStream.read(bytes);

                        ConverEnvInfo cei = new ConverEnvInfo();
                        cei.setByteBuffer(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN), 0);

                        Intent intent = new Intent("dataUpdate");
                        activity.putUpdatedData(intent, cei);
                        LocalBroadcastManager.getInstance(ApplicationUtil.this).sendBroadcast(intent);

                        activity.runOnUiThread(() -> activity.renderData(cei));

                        socket.sendUrgentData(0xFF);
                    }

            } catch (Exception e) {
                destroySocket();
                activity.runOnUiThread(() -> Toast.makeText(ApplicationUtil.this, "网络连接出错", Toast.LENGTH_LONG).show());
            }
        }).start();
    }


    //对服务器发送指令
    public void sendCommand(String cmd) {
        if (HOST == null) return;
        new Thread(() -> {
            try {
                byte[] cmdStruct = new byte[2];
                cmdStruct[1] = (byte) Integer.parseInt(cmd, 2);
                outputStream.write(cmdStruct);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void destroySocket() {
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
