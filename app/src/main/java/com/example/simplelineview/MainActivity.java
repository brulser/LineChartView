package com.example.simplelineview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * 展示布局
 */
public class MainActivity extends AppCompatActivity {
    private LineView lineView;
    private ChartLineView soundmeterview;
    private MyHandler handler = new MyHandler(this);
    private static final int MSG_START = 0;
    private static final int MSG_STOP = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundmeterview = findViewById(R.id.soundmeterview);

    }

    public void start(View view) {
        if (handler.hasMessages(MSG_START)) {
            handler.removeMessages(MSG_START);
            Toast.makeText(this, "停止", Toast.LENGTH_SHORT);
            ((TextView) (view)).setText("开始");
        } else {
            Toast.makeText(this, "开始", Toast.LENGTH_SHORT);
            handler.sendEmptyMessage(MSG_START);
            ((TextView) (view)).setText("停止");

        }
    }

    public boolean isAdd = true;

    public void mode(View view) {
        isAdd = !isAdd;
        Toast.makeText(this, "开始递增", Toast.LENGTH_SHORT);
    }

    private static class MyHandler extends Handler {
        WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity activity = weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case MSG_START:
                    //递增或递减数据
                    int value = (int) (new Random().nextInt(2) == 1 ? (activity.soundmeterview.getLastData() + new Random().nextInt(5)) : (activity.soundmeterview.getLastData() - new Random().nextInt(5)));
                    if (value > 100 || value < 0) {
                        value = 50;
                    }
                    activity.soundmeterview.addData(value);
                    sendEmptyMessageDelayed(MSG_START, 20);
                    break;
            }

        }
    }


}