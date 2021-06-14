package com.example.sockettest.device;


public interface DeviceHandler {

    //获取设备当前状态的提示
    String getDeviceTip();

    //获取设备控制码
    String[] getDeviceControlCode();


    /**
     * 在智能模式下每秒根据广播收到的数据修正设备状态
     */
    void amendDeviceOnSmartMode(float basis, boolean smartMode);


    /**
     * 在设备开关被点击下修正设备状态
     * 1、根据当前设备状态进行相应的状态转换
     * 2、向服务器发送相应的控制指令
     * 3、UI页面设备视图刷新
     */
    void amendDeviceOnClick();


}
