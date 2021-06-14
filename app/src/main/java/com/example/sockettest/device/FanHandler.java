package com.example.sockettest.device;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

public class FanHandler extends AbstractDeviceHandler {

    //  FAN_OFF     FAN_1      FAN_2      FAN_3
    private String[] fanCode = {"1","10000000","10000001","10000010","10000011"};

    public FanHandler(View view, ObjectAnimator animator) {
        super(view, animator);
    }

    @Override
    public String getDeviceTip() {
        return getFanTip();
    }

    @Override
    public String[] getDeviceControlCode() {
        return fanCode;
    }



    @Override
    public void amendDeviceOnSmartMode(float basis,boolean smartMode) {
        amendFanOnSmartMode(basis,smartMode);
    }

    @Override
    public void amendDeviceOnClick() {
        fanCode[0] = String.valueOf((Integer.parseInt(fanCode[0]) + 1) % 4);
        appUtil.sendCommand(fanCode[Integer.parseInt(fanCode[0]) == 0?4:Integer.parseInt(fanCode[0])]);
        fanAnimation(Integer.parseInt(fanCode[0]));
    }






    private void amendFanOnSmartMode(float temperature,boolean smartMode) {

        //20℃ 且风扇已被打开且处于智能模式 则关闭风扇
        if (temperature < 20 && Integer.parseInt(fanCode[0]) != 1 && smartMode) {
            Log.d("风扇","20℃ 且风扇已被打开且处于智能模式 则关闭风扇");
            fanCode[0] = String.valueOf(1);
            appUtil.sendCommand(fanCode[1]);
            fanAnimation(1);
        }
        //如果 温度>=20℃ 并且 风扇没有开 且处于智能模式，则打开风扇至1档
        else if (temperature >= 20 && Integer.parseInt(fanCode[0]) == 1 && smartMode) {
            Log.d("风扇","如果 温度>=20℃ 并且 风扇没有开 且处于智能模式，则打开风扇至1档");
            fanCode[0] = String.valueOf(2);

            System.out.println(fanCode[0] + "util_-----: " + appUtil + "beepcode 1 + ‘’‘’‘’‘’‘’‘" + fanCode[2]);
            appUtil.sendCommand(fanCode[2]);
            fanAnimation(2);
        }

        //如果 温度>=25℃  且处于智能模式，则继续增大风扇至2档
        else if (temperature >= 25 && smartMode) {
            Log.d("风扇","如果 温度>=25℃  且处于智能模式，则继续增大风扇至2档");
            fanCode[0] = String.valueOf(3);
            appUtil.sendCommand(fanCode[3]);
            fanAnimation(3);
        }

        //如果 温度>=30℃  且处于智能模式，则继续增大风扇至3档
        else if (temperature >= 30  && smartMode) {
            Log.d("风扇","如果 温度>=30℃  且处于智能模式，则继续增大风扇至3档");
            fanCode[0] = String.valueOf(0);
            appUtil.sendCommand(fanCode[4]);
            fanAnimation(0);
        }

    }








    public void fanAnimation(int speed) {

        if (speed == 1)
            animator.pause();
        if (speed == 2) {
            animator.setDuration(800);
            if (!animator.isStarted()) animator.start();
            else
                animator.resume();
        }
        if (speed == 3)
            animator.setDuration(600);
        if (speed == 0)
            animator.setDuration(400);
    }






    public String getFanTip() {
        int code = Integer.parseInt(fanCode[0]);
        String info = null;

        if (code == 1)
            info = "风扇已关闭！";
        if (code == 2)
            info = "风扇一档开启，转速400!";
        if (code == 3)
            info = "风扇二档开启，转速600!";
        if (code == 0)
            info = "风扇三档开启，转速800!";
        return info;
    }

}
