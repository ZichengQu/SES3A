package com.ses3a.u_argo.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ses3a.u_argo.MainActivity;
import com.ses3a.u_argo.R;

public class TitleView extends FrameLayout {
//    private TextView titleText;
    private ImageView back;
    private ImageView home;

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_custom_actionbar, this);
//        titleText = (TextView) findViewById(R.id.title_text);
        back = (ImageView) findViewById(R.id.back);
        home = (ImageView) findViewById(R.id.home);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Activity) getContext(), MainActivity.class);
                context.startActivity(intent);
            }
        });
    }

//    public void setTitleText(String text) {
//        titleText.setText(text);
//    }
//
//    public void hideBackImage(){
//        back.setVisibility(View.GONE);
//    }

}
