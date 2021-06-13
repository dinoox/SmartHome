package com.example.sockettest.device;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class CameraHandler extends AbstractDeviceHandler {

    private String[] cameraCode = {"1","10110000","10110001"};



    public String getCameraTip() {
        int code = Integer.parseInt(cameraCode[0]);
        String info = null;

        if (code == 1)
            info = "照相机已停止！";
        if (code == 0)
            info = "照相机已启动！";
        return info;
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
    public void amendDeviceOnSmartMode(ObjectAnimator animator,float basis, boolean smartMode) {

    }

    @Override
    public void amendDeviceOnSmartMode(ObjectAnimator animator, float basis, boolean smartMode, View view) {

    }

    @Override
    public void renderDeviceAnimation(ObjectAnimator animator, int code) {

    }

    @Override
    public void renderDeviceAnimation(ObjectAnimator animator, int code, View view) {
        cameraAnimationOfView(animator,code,view);
    }

    public void cameraAnimationOfView(ObjectAnimator animator, int code,View view) {

//        if (this.view == null) this.view = view;

        if (code == 1) {

            animator = ObjectAnimator.ofInt((TextView)view,"textColor", Color.rgb(29,180,242),Color.GRAY);
            animator.setDuration(1000);
            animator.setEvaluator(new ArgbEvaluator());
            animator.start();
        }

        if (code == 0) {
            animator = ObjectAnimator.ofInt((TextView)view,"textColor", Color.GRAY,Color.rgb(29,180,242));
            animator.setDuration(1000);
            animator.setEvaluator(new ArgbEvaluator());
            animator.start();
        }
    }

}
