package com.example.sockettest.device;

import android.animation.ObjectAnimator;
import android.view.View;

public class BeepHandler extends AbstractDeviceHandler {

    //  BEEP_OFF    BEEP_ON
    private String[] beepCode = {"1","10010001","10010000"};

    public BeepHandler(View view, ObjectAnimator animator) {
        super(view, animator);
    }


    @Override
    public String getDeviceTip() {
        return getBeepTip();
    }

    @Override
    public String[] getDeviceControlCode() {
        return beepCode;
    }

    @Override
    public void amendDeviceOnSmartMode(float basis, boolean smartMode) {
        amendBeepOnSmartMode(basis,smartMode);
    }

    @Override
    public void amendDeviceOnClick() {
        beepCode[0] = String.valueOf((Integer.parseInt(beepCode[0]) + 1) % 2);
        appUtil.sendCommand(beepCode[Integer.parseInt(beepCode[0]) == 0?2:Integer.parseInt(beepCode[0])]);

        beepAnimation(Integer.parseInt(beepCode[0]));
    }


    private void amendBeepOnSmartMode(float humidity,boolean smartMode) {
        //如果湿度>=10并且蜂鸣器没有开且处于智能模式，则打开蜂鸣器
        if (humidity >= 10 && Integer.parseInt(beepCode[0]) != 0 && smartMode) {
            System.out.println("如果湿度>=18并且蜂鸣器没有开且处于智能模式，则打开蜂鸣器");
            beepCode[0] = String.valueOf(0);
            System.out.println(beepCode[0] + "util_-----: " + appUtil + "beepcode 1 + ‘’‘’‘’‘’‘’‘" + beepCode[1]);
            appUtil.sendCommand(beepCode[2]);
            beepAnimation(0);
        }

        //如果湿度<10并且蜂鸣器已经打开了且处于智能模式，则关闭蜂鸣器
        if (humidity < 10 && Integer.parseInt(beepCode[0]) == 0 && smartMode) {
            System.out.println("如果湿度<18并且蜂鸣器已经打开了且处于智能模式，则关闭蜂鸣器");
            beepCode[0] = String.valueOf(1);
            appUtil.sendCommand(beepCode[1]);
            beepAnimation(1);
        }

    }





    public void beepAnimation(int code) {

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
