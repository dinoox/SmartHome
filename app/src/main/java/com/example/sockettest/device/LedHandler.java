package com.example.sockettest.device;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LedHandler extends AbstractDeviceHandler {

    //  LED_OFF     LED_ON
    private String[] ledCode = {"1","10100000","10100001"};



    public LedHandler(View view, ObjectAnimator animator) {
        super(view, animator);
    }

    @Override
    public String getDeviceTip() {
        return getLedTip();
    }

    @Override
    public String[] getDeviceControlCode() {
        return ledCode;
    }

    @Override
    public void amendDeviceOnSmartMode(float basis, boolean smartMode) {
        amendLedOnSmartMode(basis,smartMode);
    }

    @Override
    public void amendDeviceOnClick() {
        ledCode[0] = String.valueOf((Integer.parseInt(ledCode[0]) + 1) % 2);
        appUtil.sendCommand(ledCode[Integer.parseInt(ledCode[0]) == 0?2:Integer.parseInt(ledCode[0])]);

        ledAnimation(Integer.parseInt(ledCode[0]));
    }


    private void amendLedOnSmartMode(float ill,boolean smartMode) {

        //光照强度 <= 200 并且电灯没开且处于智能模式，则打开LED灯
        if (ill <= 200 && Integer.parseInt(ledCode[0]) == 1 && smartMode) {
            Log.d("LED","光照强度 <= 200 并且电灯没开且处于智能模式，则打开LED灯");
            ledCode[0] = String.valueOf(0);
            appUtil.sendCommand(ledCode[2]);
            ledAnimation(0);
        }
        //光照强度 > 200 并且电灯开了且处于智能模式，则关闭LED灯
        else if (ill > 200 && Integer.parseInt(ledCode[0]) == 0 && smartMode) {
            Log.d("LED","光照强度 > 200 并且电灯开了且处于智能模式，则关闭LED灯");
            ledCode[0] = String.valueOf(1);
            appUtil.sendCommand(ledCode[1]);
            ledAnimation(1);
        }

    }




    public void ledAnimation(int code) {


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


    public String getLedTip() {
        int code = Integer.parseInt(ledCode[0]);
        String info = null;

        if (code == 1)
            info = "LED灯完了！";
        if (code == 0)
            info = "LED灯亮了！";
        return info;
    }
}
