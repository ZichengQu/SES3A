package com.ses3a.u_argo.tools;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ses3a.u_argo.R;


public class CustomToast {
    private boolean canceled = true;
    private Handler handler;
    private Toast toast;
    private TimeCount time;
    private TextView toast_content;

    private static CustomToast instance;

    public static CustomToast getInstance(Context context) {
        if(instance == null) {
            instance = new CustomToast(context);
        }

        return instance;
    }

    public CustomToast(Context context) {
        this(context, new Handler());
    }

    public CustomToast(Context context, Handler handler) {
        this.handler = handler;

        View layout = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null, false);
        toast_content = (TextView) layout.findViewById(R.id.tvToastContent);
        if (toast == null) {
            toast = new Toast(context);
        }
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
    }


    public void show(String text, int duration) {
        time = new TimeCount(duration, 3000);//1000是消失渐变时间
        toast_content.setText(text);
        if (canceled) {
            time.start();
            canceled = false;
            showUntilCancel();
        }
    }


    public void hide() {
        if (toast != null) {
            toast.cancel();
        }
        canceled = true;
    }

    private void showUntilCancel() {
        if (canceled) {
            return;
        }
        toast.show();
        handler.postDelayed(new Runnable() {
            public void run() {
                showUntilCancel();
            }
        }, 1000);
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); // 总时长,计时的时间间隔
        }

        @Override
        public void onFinish() { // 计时完毕时触发
            hide();
        }

        @Override
        public void onTick(long millisUntilFinished) { // 计时过程显示
        }

    }

}

