package com.example.sockettest;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.sockettest.bean.Category;
import com.example.sockettest.bean.ConverEnvInfo;
import com.example.sockettest.util.ApplicationUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Category category;

    private TextView temperature,humidity,ill,bet,adc,x,y,z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationUtil appUtil = (ApplicationUtil) this.getApplication();
        appUtil.init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        appUtil.connectServer(this);
    }

    @Override
    public void onClick(View v) {

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

        if(!(v.getId() == R.id.control_page)) {
            Intent intent = new Intent(MainActivity.this,MyActivity.class);
            intent.putExtra("category",category.name().toLowerCase());
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(MainActivity.this,ControlActivity.class);
        startActivity(intent);
    }

    /**
     * 根据每秒请求到的数据绑定到八宫隔的TextView上
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
     * @param intent 意图对象
     * @param cei 数据
     */
    public void putUpdatedData(Intent intent,ConverEnvInfo cei) {
        intent.putExtra("temperature",cei.temperature.get());
        intent.putExtra("humidity",cei.humidity.get());
        intent.putExtra("ill",cei.ill.get());
        intent.putExtra("bet",cei.bet.get());
        intent.putExtra("adc",cei.adc.get());
        intent.putExtra("x",(float) cei.x.get());
        intent.putExtra("y",(float) cei.y.get());
        intent.putExtra("z",(float) cei.z.get());
    }

    /**
     * 初始化页面所有组件
     */
    public void initComponents() {
        int [] ids = {R.id.sensor1,R.id.sensor2,R.id.sensor3,R.id.sensor4,R.id.sensor5,R.id.sensor6,R.id.sensor7,R.id.sensor8};
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

    }
}