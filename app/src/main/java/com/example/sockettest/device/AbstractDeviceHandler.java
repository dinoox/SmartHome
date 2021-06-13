package com.example.sockettest.device;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;


import com.example.sockettest.util.ApplicationUtil;

import java.lang.reflect.Method;


public abstract class AbstractDeviceHandler implements DeviceHandler {

    public static ApplicationUtil appUtil;

    public View view;



    static {
        getCurApplication();
    }

    public static Application getCurApplication() {

        try{
            Class atClass = Class.forName("android.app.ActivityThread");
            Method currentApplicationMethod = atClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            appUtil = (ApplicationUtil) currentApplicationMethod.invoke(null);
            Log.d("fw_create","curApp class1:"+ appUtil);
        }catch (Exception e){
            Log.d("fw_create","e:"+e.toString());
        }

        if(appUtil != null)
            return appUtil;

        try{
            Class atClass = Class.forName("android.app.AppGlobals");
            Method currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
            currentApplicationMethod.setAccessible(true);
            appUtil = (ApplicationUtil) currentApplicationMethod.invoke(null);
            Log.d("fw_create","curApp class2:"+appUtil);
        }catch (Exception e){
            Log.d("fw_create","e:"+e.toString());
        }

        return appUtil;
    }


}
