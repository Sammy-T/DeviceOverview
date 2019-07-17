package sammyt.deviceoverview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Sammy on 6/30/2017.
 */

public class CustomBar extends View {

    private Paint mTextPaint;
    private Paint mPaint;
    private Paint mNegativePaint;

    private float mTextSize = 0;

    private float startNoPadding = 0;
    private float drawHeight = 0;
    private float startXneg = 0;
    private float stopXneg = 0;
    private float startX = 0;
    private float stopX = 0;
    private float textDisplayHeight = 0;

    private float mProgress = 100;
    private String mDisplayText = "";

    public CustomBar(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public void init(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        if(mTextSize == 0){
//            mTextSize = mTextPaint.getTextSize();
//        }else{
//            mTextPaint.setTextSize(mTextSize);
//        }
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                getResources().getDisplayMetrics());
        mTextPaint.setTextSize(mTextSize);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15f);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setTextSize(mTextSize);

        mNegativePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNegativePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWaveEnd));
        mNegativePaint.setStrokeWidth(15f);
        mNegativePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 0);

        int minHeight = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int height = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        drawHeight = getHeight() / 2; // Set line position
        startNoPadding = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;

        if(mProgress < 0) {
            // Draw negative line
            startXneg = getWidth() / 2; // Middle of the view
            stopXneg = startXneg + (mProgress / 100 * startNoPadding);

            canvas.drawLine(startXneg, drawHeight, stopXneg, drawHeight, mNegativePaint);
        }else{
            // Draw positive line
            startX = getWidth() / 2; // Middle of the view
            stopX = startX + (mProgress / 100 * startNoPadding);

            canvas.drawLine(startX, drawHeight, stopX, drawHeight, mPaint);
        }

        // Draw text
        textDisplayHeight = getHeight() - (getPaddingBottom() / 3);
//        mDisplayText = String.valueOf(mProgress);

        canvas.drawText(mDisplayText, getWidth() / 2, textDisplayHeight, mTextPaint);
    }

    public void setProgress(float progress, String displayText){
        mProgress = progress;
        if(displayText != null) {
            mDisplayText = displayText;
        }else{
            mDisplayText = "";
        }
        invalidate();
    }
}
