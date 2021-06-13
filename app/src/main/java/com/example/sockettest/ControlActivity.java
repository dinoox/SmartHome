package com.example.sockettest;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
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

import com.example.sockettest.device.BeepHandler;
import com.example.sockettest.device.CameraHandler;
import com.example.sockettest.device.DeviceHandler;
import com.example.sockettest.device.FanHandler;
import com.example.sockettest.device.LedHandler;
import com.example.sockettest.util.ApplicationUtil;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private boolean smartMode = false;

    private ApplicationUtil appUtil;

    private ObjectAnimator fanAnimator,ledAnimator,beepAnimator,cameraAnimator;


    private DeviceHandler fanHandler,beepHandler,ledHandler,cameraHandler;


    private ImageView fanImage,beepImage;

    private TextView ledImage,cameraImage;

    private MyBroadcast broadcast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appUtil = (ApplicationUtil) this.getApplication();
        appUtil.init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //初始化组件与动画对象
        initComponents();
        initAnimations();


        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("dataUpdate");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast= new MyBroadcast(), intentFilter);

    }

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (smartMode) {
                beepHandler.amendDeviceOnSmartMode(beepAnimator,intent.getFloatExtra("humidity", 0),smartMode);
                fanHandler.amendDeviceOnSmartMode(fanAnimator,intent.getFloatExtra("temperature", 0),smartMode);
                ledHandler.amendDeviceOnSmartMode(ledAnimator,intent.getFloatExtra("ill", 0),smartMode, ledImage);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fan:
                fanHandler.getDeviceControlCode()[0] = String.valueOf((Integer.parseInt(fanHandler.getDeviceControlCode()[0]) + 1) % 4);
                appUtil.sendCommand(fanHandler.getDeviceControlCode()[Integer.parseInt(fanHandler.getDeviceControlCode()[0]) == 0?4:Integer.parseInt(fanHandler.getDeviceControlCode()[0])]);

                fanHandler.renderDeviceAnimation(fanAnimator,Integer.parseInt(fanHandler.getDeviceControlCode()[0]));
                Toast.makeText(ControlActivity.this,fanHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.beep:
                beepHandler.getDeviceControlCode()[0] = String.valueOf((Integer.parseInt(beepHandler.getDeviceControlCode()[0]) + 1) % 2);
                appUtil.sendCommand(beepHandler.getDeviceControlCode()[Integer.parseInt(beepHandler.getDeviceControlCode()[0]) == 0?2:Integer.parseInt(beepHandler.getDeviceControlCode()[0])]);

                beepHandler.renderDeviceAnimation(beepAnimator,Integer.parseInt(beepHandler.getDeviceControlCode()[0]));
                Toast.makeText(ControlActivity.this,beepHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.led:
                ledHandler.getDeviceControlCode()[0] = String.valueOf((Integer.parseInt(ledHandler.getDeviceControlCode()[0]) + 1) % 2);
                appUtil.sendCommand(ledHandler.getDeviceControlCode()[Integer.parseInt(ledHandler.getDeviceControlCode()[0]) == 0?2:Integer.parseInt(ledHandler.getDeviceControlCode()[0])]);

                ledHandler.renderDeviceAnimation(ledAnimator,Integer.parseInt(ledHandler.getDeviceControlCode()[0]),ledImage);
                Toast.makeText(ControlActivity.this,ledHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
            case R.id.camera:
                cameraHandler.getDeviceControlCode()[0] = String.valueOf((Integer.parseInt(cameraHandler.getDeviceControlCode()[0]) + 1) % 2);
                appUtil.sendCommand(cameraHandler.getDeviceControlCode()[Integer.parseInt(cameraHandler.getDeviceControlCode()[0]) == 0?2:Integer.parseInt(cameraHandler.getDeviceControlCode()[0])]);

                cameraHandler.renderDeviceAnimation(cameraAnimator,Integer.parseInt(cameraHandler.getDeviceControlCode()[0]),cameraImage);
                Toast.makeText(ControlActivity.this,cameraHandler.getDeviceTip(),Toast.LENGTH_SHORT).show();
                break;
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        smartMode = isChecked;
        amendButtonState(smartMode);
    }




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

        System.out.println(smartSwitch);
        smartSwitch.setOnCheckedChangeListener(this);

        fanHandler = new FanHandler();
        beepHandler = new BeepHandler();
        ledHandler = new LedHandler();
        cameraHandler = new CameraHandler();
    }

    public void initAnimations() {
        //init fan animator
        fanAnimator = ObjectAnimator.ofFloat(fanImage, "rotation", 0f, 359f);
        fanAnimator.setInterpolator(new LinearInterpolator());
        fanAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        //init beep animator
        beepAnimator = ObjectAnimator.ofFloat(beepImage, "translationX", 0,5,-5,0);
        beepAnimator.setDuration(100);
        beepAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        beepAnimator.setInterpolator(new LinearInterpolator());
    }



    public void amendButtonState(boolean smartMode) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast);
    }
}