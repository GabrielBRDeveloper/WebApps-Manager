package br.com.nullexcept.webappmanager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ProgressBar extends View {
    private int value = 50;
    private int max = 100;
    private int COLOR = Color.rgb(0,112,255);
    public ProgressBar(Context context) {
        super(context);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setProgress(int progress){
        value = Math.min(progress,max);
        invalidate();
    }


    public void setMax(int max) {
        max = Math.max(1, max);
        this.max = max;
        value = Math.min(value,max);
        invalidate();
    }

    public int getProgress(){
        return value = Math.min(value,max);
    }

    public int getMax(){
        return max;
    }

    private Rect rect = new Rect();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height  = getHeight();

        int drawWith = (int) Math.round ((((double)width)/max)*value);
        paint.setColor(COLOR);
        rect.set(0,0, drawWith, height);
        canvas.drawRect(rect, paint);

    }
}
