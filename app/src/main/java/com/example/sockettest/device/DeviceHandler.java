package com.example.sockettest.device;


import android.animation.ObjectAnimator;
import android.view.View;

public interface DeviceHandler {

    //获取设备当前状态的提示
    String getDeviceTip();

    //获取设备控制码
    String[] getDeviceControlCode();


    /**
     * 在智能模式下每秒根据广播收到的数据修正设备状态  animator已经过初始化
     */
    void amendDeviceOnSmartMode(ObjectAnimator animator, float basis, boolean smartMode);

    /**
     * 在智能模式下每秒根据广播收到的数据修正设备状态  animator未经过初始化
     */
    void amendDeviceOnSmartMode(ObjectAnimator animator, float basis, boolean smartMode, View view);


    /**
     *  根据当前的设备状态渲染设备动画  animator已经过初始化
     */

    void renderDeviceAnimation(ObjectAnimator animator, int code);


    /**
     * 根据当前的设备状态渲染设备动画(带视图)   animator未经过初始化
     */
    void renderDeviceAnimation(ObjectAnimator animator, int code, View view);
}
