package com.example.simplelineview;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by 何凌 on 2016/5/26.
 */
public class ScreenUtils {

    public static int dp2px(Context context, float dp) {

        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int getScreenWidth(FragmentActivity context) {
        int screen_width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            screen_width = size.x;
        } else {
            screen_width = context.getResources().getDisplayMetrics().widthPixels;
        }
        return screen_width;
    }

    public static int getScreenHeight(FragmentActivity context) {
        int screen_height = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            screen_height = size.y;
        } else {
            screen_height = context.getResources().getDisplayMetrics().heightPixels;
        }
        return screen_height;
    }

    public static int[] getScreenSize(FragmentActivity context) {
        int[] box = new int[2];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            box[0] = size.x;
            box[1] = size.y;
        } else {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            box[0] = metrics.widthPixels;
            box[1] = metrics.heightPixels;
        }
        return box;
    }
}
