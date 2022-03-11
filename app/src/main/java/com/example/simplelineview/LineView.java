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
 * 折线图
 */
public class LineView extends View {

    private int mHeight;

    private int mWidth;

    /**
     * 纵坐标最大值
     */
    private int mMax = 200;

    /**
     * 数据源里面的最小值
     */
    private int mDataMin = 0;

    /**
     * 数据源里面的最大值
     */
    private int mDataMax = 0;

    public int getMax() {
        return mMax;
    }

    /**
     * 设置纵坐标最大值
     *
     * @param mMax 当前数据最大值
     */
    public void setMax(int mMax) {
        this.mMax = (mMax / 100 + 1) * 100;
    }

    private int widLength = 50;

    private List<Integer> list = new ArrayList<>();

    /**
     * 预设数据
     *
     * @param list
     */
    public void setList(List<Integer> list) {
        this.list = list;
        invalidate();
    }

    /**
     * 填充数据
     *
     * @param num
     */
    public void insertData(int num) {
        if (list.size() < widLength) {
            list.add(num);
        } else {
            list.remove(0);
            list.add(num);
        }
        forMartMax(num);
        invalidate();
    }

    public int getLastData() {
        return list.size() == 0 ? 0 : list.get(list.size() - 1);
    }

    /**
     * 纵坐标最大值根据数据最大值计算而来
     * 如果数据超过最大值 改变最大值
     *
     * @param num
     */
    private void forMartMax(int num) {
        if (mDataMin == 0) {
            mDataMin = num;
        }
        if (mDataMax == 0) {
            mDataMax = num;
        }
        if (num < mDataMin) {
            mDataMin = num;
//            avg = (int) BigDecimalUtils.div(max + min, 2, 0);
        } else if (num > mDataMax) {
            mDataMax = num;
//            avg = (int) BigDecimalUtils.div(max + min, 2, 0);
        }
        //如果最大值超过了纵坐标限额 需要重新设置纵坐标最大值
        if (mDataMax > mMax) {
            setMax(mDataMax);
        }

    }


    public LineView(Context context) {
        super(context);
        initPaint();
    }


    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }


    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mHeight = canvas.getHeight();
        mWidth = canvas.getWidth();

        Rect rect_max = getRect(mMax + "");
        //绘制每一份高度 分割成max个纵向点
        float perHeight = (float) BigDecimalUtils.div(mHeight - rect_max.height(), mMax, 2);
        //绘制每一份宽度 分成50个横向点
        float perWidth = (float) BigDecimalUtils.div(mWidth - rect_max.width() - 30, widLength, 2);


        //绘制基本线条&数据
        drawBaseLine(canvas);


        //绘制绿色线条 start
        Path path = new Path();
        float oldHeight = 0;
        float oldWidth = 0;

        //加入绿色线条
        for (int i = 0; i < list.size(); i++) {
            float dramHeight = (float) BigDecimalUtils.mul(perHeight, mMax - list.get(i)) + rect_max.height() / 2;
            float dramWidth = (float) BigDecimalUtils.mul(perWidth, i) + rect_max.width() + 30;

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
//            LogUtils.d("width = " + dramWidth + "   height === " + dramHeight);
        }

        canvas.drawPath(path, paint_line);
        //绘制绿色线条 end


        super.onDraw(canvas);
    }

    private Paint paint_line;
    private Paint paint_BaseChartline;
    private Paint paint_TextChart;

    //初始化画笔
    private void initPaint() {
        paint_line = new Paint();
        paint_line.setColor(Color.GREEN);
        paint_line.setStyle(Paint.Style.STROKE);
        paint_line.setAntiAlias(true);
        paint_line.setStrokeJoin(Paint.Join.ROUND);
        paint_line.setStrokeWidth(6);

        paint_BaseChartline = new Paint();
        paint_BaseChartline.setColor(getResources().getColor(R.color.color_grey_999999));
        paint_BaseChartline.setAntiAlias(true);
        paint_BaseChartline.setStrokeWidth(4);

        paint_TextChart = new Paint();
        paint_TextChart.setColor(getResources().getColor(R.color.color_grey_999999));
        paint_TextChart.setTextSize(ScreenUtils.dp2px(getContext(), 11));

    }

    //绘制基本线条
    private void drawBaseLine(Canvas canvas) {

        Rect rect_max = getRect(mMax + "");
        //减去首位字符串高度
        int baseHeight = mHeight - rect_max.height();
        for (int i = 0; i <= 5; i++) {
            float mul = (float) BigDecimalUtils.mul(BigDecimalUtils.div(baseHeight, 5, 0), i, 0);
            Rect rect_cur = getRect(mMax / 5 * (5 - i) + "");
            //数字是从高低排序的
            canvas.drawText(mMax / 5 * (5 - i) + "", rect_max.width() - rect_cur.width(), Math.abs(rect_max.top) + mul, paint_TextChart);
//            canvas.drarect
        }

        for (int i = 0; i <= 5; i++) {
            paint_BaseChartline.setColor(getResources().getColor(R.color.color_grey_999999));
            float mul = (float) BigDecimalUtils.mul(BigDecimalUtils.div(baseHeight, 5, 0), i, 0);
            canvas.drawLine(rect_max.width() + 30, mul + rect_max.height() / 2, mWidth, mul + rect_max.height() / 2, paint_BaseChartline);
        }
        //绘制竖线
        canvas.drawLine(rect_max.width() + 30, rect_max.height() / 2, rect_max.width() + 30, mHeight - rect_max.height() / 2, paint_BaseChartline);
    }


    private Rect getRect(String str) {
        Rect rect = new Rect();
        paint_TextChart.getTextBounds(str, 0, str.length(), rect);
//        int w = rect.width();
//        int h = rect.height();
        return rect;
    }


}
