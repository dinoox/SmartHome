package com.example.sockettest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.sockettest.bean.Category;
import java.util.ArrayList;
import java.util.Objects;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class MyActivity extends AppCompatActivity {

    private float [] score = {0,0,0,0,0,0,0,0,0,0};

    private String [] data = {"9s前","8s前","7s前","6s前","5s前","4s前","3s前","2s前","1s前","现在"};


    private ArrayList<AxisValue> mAxisValues = new ArrayList<>();
    private ArrayList<PointValue> pointValues = new ArrayList<>();

    private Axis xAxis = new Axis();

    private Line mLine = new Line();

    private ArrayList<Line> mLines = new ArrayList<Line>();

    private LineChartData mData = new LineChartData();

    private LineChartView chart;

    private Category category;

    private MyBroadcast broadcast = new MyBroadcast();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        chart = findViewById(R.id.chart);
        category = Category.valueOf(Objects.requireNonNull(getIntent().getStringExtra("category")).toUpperCase());

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("dataUpdate");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast,intentFilter);

        setChartOptions();
    }

    public void setChartOptions() {
        for(int i =0; i<10; i++){
            mAxisValues.add(new AxisValue(i).setLabel(data[i]));
            pointValues.add(new PointValue(i,score[i]));
        }

        mLine.setValues(pointValues);
        mLine.setColor(Color.parseColor("#5699BB"));
        mLine.setShape(ValueShape.DIAMOND);
        mLine.setCubic(true);
        mLine.setFilled(true);
        mLine.setHasLabels(true);
        mLines.add(mLine);

        xAxis.setValues(mAxisValues);
        xAxis.setHasTiltedLabels(true);
        xAxis.setTextColor(Color.parseColor("#5699BB"));
        xAxis.setHasLines(true);
        xAxis.setTextSize(15);

        mData.setLines(mLines);
        mData.setAxisXBottom(xAxis);

        chart.setZoomType(ZoomType.HORIZONTAL);
        chart.setMaxZoom(2);
        chart.setLineChartData(mData);
    }

    public void addPoint(float newData) {
        for(int i = 0; i < pointValues.size()-1; i++)
            pointValues.set(i,pointValues.get(i).set(i,pointValues.get(i+1).getY()));

        pointValues.set(pointValues.size()-1,new PointValue(pointValues.size()-1,newData));
        chart.setLineChartData(mData);
    }

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            addPoint(intent.getFloatExtra(category.name().toLowerCase(),0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast);
    }
}
