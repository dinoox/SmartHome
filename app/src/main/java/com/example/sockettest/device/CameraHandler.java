package com.example.sockettest.device;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class CameraHandler extends AbstractDeviceHandler {

    //CAMERA_OFF   CAMERA_ON
    private String[] cameraCode = {"1","10110000","10110001"};

    public CameraHandler(View view) {
        super(view);
        initDeviceAnimator();
    }

    @Override
    public String getDeviceTip() {
        return getCameraTip();
    }

    @Override
    public String[] getDeviceControlCode() {
        return cameraCode;
    }

    @Override
    public void amendDeviceOnSmartMode(float basis, boolean smartMode) {

    }

    @Override
    public void amendDeviceOnClick() {
        cameraCode[0] = String.valueOf((Integer.parseInt(cameraCode[0]) + 1) % 2);
        appUtil.sendCommand(cameraCode[Integer.parseInt(cameraCode[0]) == 0?2:Integer.parseInt(cameraCode[0])]);
        cameraAnimation(Integer.parseInt(cameraCode[0]));
    }

    @Override
    public void initDeviceAnimator() {
    }


    public void cameraAnimation(int code) {

        if (code == 1) {
            animator = ObjectAnimator.ofInt((TextView)view,"textColor", Color.rgb(29,180,242),Color.GRAY);
            animator.setDuration(1000);
            animator.setEvaluator(new ArgbEvaluator());
            animator.start();
            return;
        }

        if (code == 0) {
            animator = ObjectAnimator.ofInt((TextView)view,"textColor", Color.GRAY,Color.rgb(29,180,242));
            animator.setDuration(1000);
            animator.setEvaluator(new ArgbEvaluator());
            animator.start();
        }
    }

    public String getCameraTip() {
        int code = Integer.parseInt(cameraCode[0]);
        String info = null;

        if (code == 1)
            info = "照相机已停止！";
        if (code == 0)
            info = "照相机已启动！";
        return info;
    }
}
