package com.example.sockettest.bean;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

public class IconView extends androidx.appcompat.widget.AppCompatTextView {

    public IconView(Context context) {
        super(context);
        init(context);
    }
    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/iconfont.ttf"));
    }

}
