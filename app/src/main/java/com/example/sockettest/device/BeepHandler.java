package com.example.sockettest.device;

import android.animation.ObjectAnimator;
import android.view.View;

public class BeepHandler extends AbstractDeviceHandler {

    //  BEEP_OFF    BEEP_ON
    private String[] beepCode = {"1","10010001","10010000"};


    @Override
    public String getDeviceTip() {
        return getBeepTip();
    }

    @Override
    public String[] getDeviceControlCode() {
        return beepCode;
    }

    @Override
    public void amendDeviceOnSmartMode(ObjectAnimator animator,float basis, boolean smartMode) {
        amendBeepOnSmartMode(animator,basis,smartMode);
    }

    @Override
    public void amendDeviceOnSmartMode(ObjectAnimator animator, float basis, boolean smartMode, View view) {

    }

    private void amendBeepOnSmartMode(ObjectAnimator animator,float humidity,boolean smartMode) {
        //如果湿度>=10并且蜂鸣器没有开且处于智能模式，则打开蜂鸣器
        if (humidity >= 10 && Integer.parseInt(beepCode[0]) != 0 && smartMode) {
            System.out.println("如果湿度>=18并且蜂鸣器没有开且处于智能模式，则打开蜂鸣器");
            beepCode[0] = String.valueOf(0);
            System.out.println(beepCode[0] + "util_-----: " + appUtil + "beepcode 1 + ‘’‘’‘’‘’‘’‘" + beepCode[1]);
            appUtil.sendCommand(beepCode[2]);
            beepAnimation(animator,0);
        }

        //如果湿度<10并且蜂鸣器已经打开了且处于智能模式，则关闭蜂鸣器
        if (humidity < 10 && Integer.parseInt(beepCode[0]) == 0 && smartMode) {
            System.out.println("如果湿度<18并且蜂鸣器已经打开了且处于智能模式，则关闭蜂鸣器");
            beepCode[0] = String.valueOf(1);
            appUtil.sendCommand(beepCode[1]);
            beepAnimation(animator,1);
        }

    }


    @Override
    public void renderDeviceAnimation(ObjectAnimator animator, int code) {
        beepAnimation(animator,code);
    }

    @Override
    public void renderDeviceAnimation(ObjectAnimator animator, int code, View view) {

    }

    public void beepAnimation(ObjectAnimator animator,int code) {

        System.out.println(" beep animator:   " + animator + "code : " + code );
        if (code == 1) {
            animator.pause();
            return;
        }

        if (!animator.isStarted())
            animator.start();
        else
            animator.resume();
    }

    public String getBeepTip() {
        int code = Integer.parseInt(beepCode[0]);
        String info = null;

        if (code == 1)
            info = "蜂鸣器已停止！";
        if (code == 0)
            info = "蜂鸣器已启动！";
        return info;
    }
}
