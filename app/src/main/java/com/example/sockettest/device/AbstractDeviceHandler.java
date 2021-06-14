package com.example.sockettest.device;

import android.animation.ObjectAnimator;
import android.app.Application;
import android.util.Log;
import android.view.View;
import com.example.sockettest.util.ApplicationUtil;
import java.lang.reflect.Method;

/**
 * 所有设备处理器的父抽象类
 * 管理唯一的当前应用的Application对象
 * 每个设备管理器对应着一个其所要管理的视图对象，以及如何对待视图（视图动画）的对象
 */

public abstract class AbstractDeviceHandler implements DeviceHandler {

    protected static ApplicationUtil appUtil;

    protected View view;

    protected ObjectAnimator animator;


    public AbstractDeviceHandler(View view, ObjectAnimator animator) {
        this.view = view;
        this.animator = animator;
    }

    //获取当前应用专属的Application
    static {
        getCurApplication();
    }

    public static Application getCurApplication() {

        try {
            Class atClass = Class.forName("android.app.ActivityThread");
            Method currentApplicationMethod = atClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            appUtil = (ApplicationUtil) currentApplicationMethod.invoke(null);
        } catch (Exception e) {
            Log.d("fw_create","e:"+e.toString());
        }

        if(appUtil != null)
            return appUtil;

        try {
            Class atClass = Class.forName("android.app.AppGlobals");
            Method currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
            currentApplicationMethod.setAccessible(true);
            appUtil = (ApplicationUtil) currentApplicationMethod.invoke(null);
        } catch (Exception e) {
            Log.d("fw_create","e:"+e.toString());
        }

        return appUtil;
    }


}
