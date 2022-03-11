package com.example.simplelineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

/**
 * @Package: com.brulser.soundmeter.ui.view
 * @ClassName: SoundMeterLineView
 * @Description: 波形图
 * @Author: Yaoshun
 * @CreateDate: 8/11/21 12:22 AM
 */
public class ChartLineView extends View {

    public ChartLineView(Context context) {
        super(context);
        initPaint();

    }

    public ChartLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();

    }

    public ChartLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

    }

    //边框笔
    private Paint Paint_Rect_Border;
    //中粗
    private Paint Paint_Rect_Border_middle;
    //细
    private Paint Paint_Rect_Border_small;
    //文字绘制
    private Paint Paint_Text_Level;
    //主线条绘制
    private Paint Paint_line_main;
    //主线条幕布
    private Paint Paint_line_alpha;
    //纵坐标固定值
    private String mLevel[] = new String[]{
            "100", "80", "60", "40", "20", "0"
    };

    private void initPaint() {

        Paint_Rect_Border = new Paint();
        Paint_Rect_Border.setColor(getResources().getColor(R.color.white));
        Paint_Rect_Border.setAntiAlias(true);
        Paint_Rect_Border.setStrokeWidth(3);
        Paint_Rect_Border.setStyle(Paint.Style.STROKE);

        Paint_Rect_Border_middle = new Paint();
        Paint_Rect_Border_middle.setColor(getResources().getColor(R.color.white));
        Paint_Rect_Border_middle.setAntiAlias(true);
        Paint_Rect_Border_middle.setStrokeWidth(2);
        Paint_Rect_Border_middle.setStyle(Paint.Style.STROKE);

        Paint_Rect_Border_small = new Paint();
        Paint_Rect_Border_small.setColor(getResources().getColor(R.color.color_grey_999999));
        Paint_Rect_Border_small.setAntiAlias(true);
        Paint_Rect_Border_small.setStrokeWidth(2);
        Paint_Rect_Border_small.setStyle(Paint.Style.STROKE);

        Paint_Text_Level = new Paint();
        Paint_Text_Level.setColor(getResources().getColor(R.color.white));
        Paint_Text_Level.setTextSize(ScreenUtils.dp2px(getContext(), 14));

        Paint_line_main = new Paint();
        Paint_line_main.setColor(Color.WHITE);
        Paint_line_main.setStyle(Paint.Style.STROKE);
        Paint_line_main.setAntiAlias(true);
        Paint_line_main.setStrokeJoin(Paint.Join.ROUND);
        Paint_line_main.setStrokeWidth(3);

        Paint_line_alpha = new Paint();
        Paint_line_alpha.setColor(Color.WHITE);
        Paint_line_alpha.setAlpha(60);
        Paint_line_alpha.setStyle(Paint.Style.FILL);
        Paint_line_alpha.setAntiAlias(true);
        Paint_line_alpha.setStrokeJoin(Paint.Join.ROUND);
//        Paint_line_alpha.setStrokeWidth(2);
    }


    //默认高宽padding 为10
    public static final int VIEWPADDING = 10;

    //绘制布局
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取除去padding 之后宽度和高度
        int width = getWidth() - VIEWPADDING;
        int height = getHeight() - VIEWPADDING;
        //绘制外框
        Rect rect = new Rect(VIEWPADDING, VIEWPADDING, width, height);
        canvas.drawRect(rect, Paint_Rect_Border);

        drawLine(canvas, width, height);

        drawText(canvas, width, height);
        if (list.size() == 0) {
            //如果还没有添加数据，就不要画了
            return;
        }
        drawData(canvas, width, height);
    }

    /**
     * 显示的数据源
     */
    private List<Double> list = new ArrayList<>();
    /**
     * 所有数据源
     */
    private List<Double> wholeList = new ArrayList<>();
    //这个最好和秒数相关，这样纵坐标可以显示时间
    private int maxListNum = 500;

    public void addData(double data) {
        if (list.size() >= maxListNum) {
            //移除第一位
            list.remove(0);
        }
        list.add(data);
        wholeList.add(data);

        invalidate();
    }

    public double getLastData() {
        return list.size() == 0 ? 0 : list.get(list.size() - 1);
    }


    public double getConsultData() {
        if (wholeList.size() == 0) {
            return 0;
        }
        double data = 0d;
        for (double d : wholeList) {
            data = BigDecimalUtils.add(d, data);
        }
        return BigDecimalUtils.div(data, wholeList.size(), 1);
    }

    /**
     * 绘制波形线条
     */
    private void drawData(Canvas canvas, int width, int height) {
        int startWidth = (int) BigDecimalUtils.div(width, 8, 0);

        Path path = new Path();
        double perWidth = BigDecimalUtils.div(width - startWidth, maxListNum, 2);
        //将总高度分为100份 每份高度(分贝)是perHeight
        double perHeight = BigDecimalUtils.div(height - VIEWPADDING, 100, 2);
        //起点线
        //旧的坐标
        float oldHeight = 0;
        float oldWidth = 0;
        //起点
        //得到移动的点
        for (int i = 0; i < list.size(); i++) {
            float dramWidth = (float) perWidth * i + startWidth;
            float dramHeight = (float) (BigDecimalUtils.sub(100, list.get(i)) * perHeight);

            if (i == 0) {
                path.moveTo(dramWidth, dramHeight);
            } else {

                //贝塞尔曲线链接
                float controlX = (oldWidth + dramWidth) / 2;
                path.cubicTo(controlX, oldHeight, controlX, dramHeight, dramWidth, dramHeight);
//                path.quadTo(oldWidth, oldHeight, dramWidth, dramHeight);
            }
            oldWidth = dramWidth;
            oldHeight = dramHeight;

        }
        canvas.drawPath(path, Paint_line_main);
        path.lineTo(oldWidth, height);
        path.lineTo(startWidth, height);
        path.close();
        canvas.drawPath(path, Paint_line_alpha);

    }


    /**
     * 绘制文本
     *
     * @param canvas
     * @param width
     * @param height
     */
    private void drawText(Canvas canvas, int width, int height) {
        //起点线 并且偏移一点
        int startWidth = (int) BigDecimalUtils.div(width, 8, 0) - VIEWPADDING;
        double perHeight = BigDecimalUtils.div(height - VIEWPADDING, 5, 0);

        for (int i = 0; i < mLevel.length; i++) {


//            canvas.drawLine(startWidth, (int) (VIEWPADDING + i * perHeight), width, (int) (VIEWPADDING + i * perHeight), Paint_Rect_Border_small);

            //获取文案高宽
            Rect rect = getRect(mLevel[i]);
            if (i == 0) {
                canvas.drawText(mLevel[i], startWidth - rect.width() - VIEWPADDING, (int) (VIEWPADDING * 2 + i * perHeight + rect.height()), Paint_Text_Level);
            } else if (i == mLevel.length - 1) {
                canvas.drawText(mLevel[i], startWidth - rect.width() - VIEWPADDING, (int) (i * perHeight), Paint_Text_Level);
            } else {
                canvas.drawText(mLevel[i], startWidth - rect.width() - VIEWPADDING, (int) (VIEWPADDING + i * perHeight + rect.height() / 2), Paint_Text_Level);
            }
        }
    }


    private Rect getRect(String str) {
        Rect rect = new Rect();
        Paint_Text_Level.getTextBounds(str, 0, str.length(), rect);
//        int w = rect.width();
//        int h = rect.height();
        return rect;
    }

    /**
     * 绘制横线和竖线
     *
     * @param canvas
     * @param width
     * @param height
     */
    private void drawLine(Canvas canvas, int width, int height) {
        //绘制6道竖线 包括一道起点竖线
        int startWidth = (int) BigDecimalUtils.div(width, 8, 0);
        int preWidth = (int) BigDecimalUtils.div(width - startWidth, 6);
        canvas.drawLine(startWidth, VIEWPADDING, startWidth, height, Paint_Rect_Border_middle);
        //绘制竖线
        for (int i = 1; i < 6; i++) {
            if (i == 1) {
                Rect rect = getRect("(Unit)");

                //画第一条线的时候，顺便画上（单位）
                canvas.drawText("(Unit)", startWidth + VIEWPADDING, VIEWPADDING + rect.height(), Paint_Text_Level);
            }

            Rect rect = getRect((i * 5) + "");
            canvas.drawText(i * 5 + "", startWidth + (preWidth * i) - rect.width() - VIEWPADDING, height - VIEWPADDING, Paint_Text_Level);
            canvas.drawLine(startWidth + (preWidth * i), VIEWPADDING, startWidth + (preWidth * i), height, Paint_Rect_Border_small);
        }


        //绘制横线
        double perHeight = BigDecimalUtils.div(height - VIEWPADDING, 5, 0);
        for (int i = 1; i < 5; i++) {
            if (i == 4) {
                Rect rect = getRect("(sec)");
                //画第一条线的时候，顺便画上（秒）
                canvas.drawText("(sec)", width - rect.width() - VIEWPADDING, (int) ((i + 1) * perHeight) - VIEWPADDING, Paint_Text_Level);
            }
            canvas.drawLine(startWidth, (int) (VIEWPADDING + i * perHeight), width, (int) (VIEWPADDING + i * perHeight), Paint_Rect_Border_small);
        }
    }

    public void clearData() {
        wholeList.clear();
        list.clear();
        invalidate();
    }
}
