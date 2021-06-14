package com.example.sockettest;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.sockettest.device.AbstractDeviceHandler;
import com.example.sockettest.device.BeepHandler;
import com.example.sockettest.device.CameraHandler;
import com.example.sockettest.device.DeviceHandler;
import com.example.sockettest.device.FanHandler;
import com.example.sockettest.device.LedHandler;
import com.example.sockettest.util.ApplicationUtil;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private boolean smartMode = false;

    private ApplicationUtil appUtil;

    private AbstractDeviceHandler fanHandler,beepHandler,ledHandler,cameraHandler;


    private ImageView fanImage,beepImage;

    private TextView ledImage,cameraImage;

    private MyBroadcast broadcast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appUtil = (ApplicationUtil) this.getApplication();
        appUtil.init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //初始化组件与设备处理器对象
        initComponents();
        initHandlers();


        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("dataUpdate");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast= new MyBroadcast(), intentFilter);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fan:
                fanHandler.amendDeviceOnClick();
                Toast.makeText(ControlActivity.this,fanHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.beep:
                beepHandler.amendDeviceOnClick();
                Toast.makeText(ControlActivity.this,beepHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.led:
                ledHandler.amendDeviceOnClick();
                Toast.makeText(ControlActivity.this,ledHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.camera:

                Toast.makeText(ControlActivity.this,cameraHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
        }


    }

    /**
     * 当Switch被点击时的回调接口
     * @param isChecked 当前状态
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        smartMode = isChecked;
        amendButtonState();
    }


    /**
     * 初始化页面所有需要的视图组件
     * （设置相应点击事件）
     */
    public void initComponents() {
        findViewById(R.id.fan).setOnClickListener(this);
        findViewById(R.id.beep).setOnClickListener(this);
        findViewById(R.id.led).setOnClickListener(this);
        findViewById(R.id.camera).setOnClickListener(this);

        fanImage = findViewById(R.id.fanImage);
        ledImage = findViewById(R.id.ledImage);
        beepImage = findViewById(R.id.beepImage);
        cameraImage = findViewById(R.id.cameraImage);

        fanImage.setOnClickListener(this);
        ledImage.setOnClickListener(this);
        beepImage.setOnClickListener(this);
        cameraImage.setOnClickListener(this);

        Switch smartSwitch = findViewById(R.id.smartSwitch);
        smartSwitch.setOnCheckedChangeListener(this);
    }


    /**
     * 初始化所有的设备控制器
     * LED设备控制器
     * 风扇设备控制器
     * 照相机设备控制器
     * 蜂鸣器设备控制器
     */
    public void initHandlers() {

        //init fan handler
        ObjectAnimator fanAnimator = ObjectAnimator.ofFloat(fanImage, "rotation", 0f, 359f);
        fanAnimator.setInterpolator(new LinearInterpolator());
        fanAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        fanHandler = new FanHandler(fanImage,fanAnimator);

        //init beep handler
        ObjectAnimator beepAnimator = ObjectAnimator.ofFloat(beepImage, "translationX", 0,5,-5,0);
        beepAnimator.setDuration(100);
        beepAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        beepAnimator.setInterpolator(new LinearInterpolator());

        beepHandler = new BeepHandler(beepImage,beepAnimator);

        //init led handler
        ledHandler = new LedHandler(ledImage,new ObjectAnimator());

        //init camera handler
        cameraHandler = new CameraHandler(cameraImage,new ObjectAnimator());
    }


    /**
     * 根据当前是否处在智能模式修正按钮状态
     * （智能模式按钮禁用）
     */
    public void amendButtonState() {
        if (smartMode) {
            findViewById(R.id.fan).setEnabled(false);
            findViewById(R.id.beep).setEnabled(false);
            findViewById(R.id.led).setEnabled(false);
//            findViewById(R.id.camera).setEnabled(false);
        } else {
            findViewById(R.id.fan).setEnabled(true);
            findViewById(R.id.beep).setEnabled(true);
            findViewById(R.id.led).setEnabled(true);
//            findViewById(R.id.camera).setEnabled(true);
        }
    }


    /**
     * 页面摧毁时刻注销广播资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast);
    }



    /**
     * 广播接受类
     */
    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            /**
             * 接收到每秒的广播后如果当前是智能模式则：
             * 以温度为指标，修正风扇的状态
             * 以湿度为指标，修正蜂鸣器的状态
             * 以光照为指标，修正LED灯的状态
             */

            if (smartMode) {
                beepHandler.amendDeviceOnSmartMode(intent.getFloatExtra("humidity", 0),smartMode);
                fanHandler.amendDeviceOnSmartMode(intent.getFloatExtra("temperature", 0),smartMode);
                ledHandler.amendDeviceOnSmartMode(intent.getFloatExtra("ill", 0),smartMode);
            }
        }
    }

}