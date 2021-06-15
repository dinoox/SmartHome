package com.example.sockettest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sockettest.bean.Category;
import com.example.sockettest.bean.ConverEnvInfo;
import com.example.sockettest.bean.IconView;
import com.example.sockettest.util.ApplicationUtil;

import java.io.IOException;
import java.lang.reflect.Field;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Category category;

    private ImageView config_server;

    private ApplicationUtil appUtil;

    private IconView server_flag;

    private TextView temperature, humidity, ill, bet, adc, x, y, z, server_info;



    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("onCreate : " + this);

        appUtil = (ApplicationUtil) this.getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        if (ApplicationUtil.HOST != null) {
            appUtil.ConnectServer(this);
            server_info.setText(ApplicationUtil.HOST + ":" + ApplicationUtil.PORT);
            server_flag.setTextColor(Color.GREEN);
            return;
        }

        config_server.performClick();
    }


    /**
     * 无论点击什么按钮都无法让dialog消失，那么怎么实现在点击取消按钮时dialog消失呢？
     * 这个消失事件受到Dialog父类中的mShowing字段的控制，通过反射获取修改即可
     */
    public void changeDialogShowState(DialogInterface dialog,boolean state) {
        try {
                Field field = Dialog.class.getDeclaredField("mShowing");
                field.setAccessible(true);
                field.set(dialog, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    @SuppressLint("SetTextI18n")
    public void onClick(View v) {

        if (v.getId() == R.id.config_server) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.acticity_dialog, null);

            EditText host = view.findViewById(R.id.host);
            EditText port = view.findViewById(R.id.port);

            host.setText(ApplicationUtil.HOST == null ? "" : ApplicationUtil.HOST);
            if (ApplicationUtil.PORT != 0) port.setText(String.valueOf(ApplicationUtil.PORT));

            builder.setPositiveButton("确定", (dialog, which) -> {

                if (!checkHost(host.getText().toString()) || !checkPort(port.getText().toString())) {

                    changeDialogShowState(dialog,false);
                    Toast.makeText(this,"IP或者端口号不合法，请修正！",Toast.LENGTH_SHORT).show();
                    return;
                }

                changeDialogShowState(dialog,true);
                ApplicationUtil.HOST = host.getText().toString();
                ApplicationUtil.PORT = Integer.parseInt(port.getText().toString());
                appUtil.ConnectServer(this);

                server_info.setText(ApplicationUtil.HOST + ":" + ApplicationUtil.PORT);
                server_flag.setTextColor(Color.GREEN);


            }).setNegativeButton("取消", (dialog, which) -> changeDialogShowState(dialog,true))
              .setTitle("请配置服务器地址").setView(view).show();

            return;
        }

        switch (v.getId()) {
            case R.id.sensor1:
                category = Category.TEMPERATURE;
                break;
            case R.id.sensor2:
                category = Category.HUMIDITY;
                break;
            case R.id.sensor3:
                category = Category.ILL;
                break;
            case R.id.sensor4:
                category = Category.BET;
                break;
            case R.id.sensor5:
                category = Category.ADC;
                break;
            case R.id.sensor6:
                category = Category.X;
                break;
            case R.id.sensor7:
                category = Category.Y;
                break;
            case R.id.sensor8:
                category = Category.Z;
                break;

        }

        if (!(v.getId() == R.id.control_page)) {
            Intent intent = new Intent(this, MyActivity.class);
            intent.putExtra("category", category.name().toLowerCase());
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(this, ControlActivity.class);
        startActivity(intent);
    }


    public boolean checkHost(String s) {
        return s.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    }

    public boolean checkPort(String s) {
        return s.matches("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
    }



    /**
     * 根据每秒请求到的数据绑定到八宫隔的TextView上
     *
     * @param cei 数据
     */
    public void renderData(ConverEnvInfo cei) {
        temperature.setText(String.format("%s", cei.temperature.get()));
        humidity.setText(String.format("%s", cei.humidity.get()));
        ill.setText(String.format("%s", cei.ill.get()));
        bet.setText(String.format("%s", cei.bet.get()));
        adc.setText(String.format("%s", cei.adc.get()));
        x.setText(String.format("%s", cei.x.get()));
        y.setText(String.format("%s", cei.y.get()));
        z.setText(String.format("%s", cei.z.get()));
    }

    /**
     * 封装每秒一到的数据到广播意图对象
     *
     * @param intent 意图对象
     * @param cei    数据
     */
    public void putUpdatedData(Intent intent, ConverEnvInfo cei) {
        intent.putExtra("temperature", cei.temperature.get());
        intent.putExtra("humidity", cei.humidity.get());
        intent.putExtra("ill", cei.ill.get());
        intent.putExtra("bet", cei.bet.get());
        intent.putExtra("adc", cei.adc.get());
        intent.putExtra("x", (float) cei.x.get());
        intent.putExtra("y", (float) cei.y.get());
        intent.putExtra("z", (float) cei.z.get());
    }


    /**
     * 页面销毁时释放socket，使得广播线程结束工作
     * 当页面再次被创建时建立新的连接
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            appUtil.socket.close();
            appUtil.socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化页面所有组件
     */
    public void initComponents() {
        int[] ids = {R.id.sensor1, R.id.sensor2, R.id.sensor3, R.id.sensor4, R.id.sensor5, R.id.sensor6, R.id.sensor7, R.id.sensor8};
        for (int id : ids)
            findViewById(id).setOnClickListener(this);

        temperature = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        ill = findViewById(R.id.ill);
        bet = findViewById(R.id.bet);
        adc = findViewById(R.id.adc);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);

        findViewById(R.id.control_page).setOnClickListener(this);

        config_server = findViewById(R.id.config_server);
        config_server.setOnClickListener(this);

        server_info = findViewById(R.id.server_info);
        server_flag = findViewById(R.id.server_flag);
    }
}