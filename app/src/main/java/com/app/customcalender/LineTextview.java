package com.app.customcalender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class LineTextview extends TextView {
    private Paint mPaint;
    private int mColor;
    private Context context;

    public LineTextview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColor = getResources().getColor(R.color.text_enable);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Slash coordinates
        int startX, startY, endX, endY;

        //View width
        int width = getWidth();
        int height = getHeight();

        //Calculating coordinates
        startX = width;
        startY = dip2px(context, 2);
        endX = 0;
        endY = height - dip2px(context, 3);
        //Slash
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
