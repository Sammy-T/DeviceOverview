package sammyt.deviceoverview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CustomVertBar extends View {

    private Paint mTextPaint;
    private Paint mPaint;
    private Paint mNegativePaint;

    private float mTextSize = 0;

    private float startNoPadding = 0;
    private float drawX = 0;
    private float startYneg = 0;
    private float stopYneg = 0;
    private float startY = 0;
    private float stopY = 0;
    private float textDisplayHeight = 0;
    private float textTopDisplayHeight = 0;

    private float mProgress = 100;
    private String mDisplayTextTop = "";
    private String mDisplayText = "";

    public CustomVertBar(Context context, AttributeSet attrs){
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

        drawX = (float) getWidth() / 2; // Set line position
        startNoPadding = (float) (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;


        if(mProgress < 0) {
            // Draw negative line
            startYneg = (float) getHeight() / 2; // Middle of the view
            stopYneg = startYneg - (mProgress / 100 * startNoPadding);

//            canvas.drawLine(startXneg, drawHeight, stopXneg, drawHeight, mNegativePaint);
            canvas.drawLine(drawX, startYneg, drawX, stopYneg, mNegativePaint);
        }else{
            // Draw positive line
            startY = (float) getHeight() / 2; // Middle of the view
            stopY = startY - (mProgress / 100 * startNoPadding);

//            canvas.drawLine(startX, drawHeight, stopX, drawHeight, mPaint);
            canvas.drawLine(drawX, startY, drawX, stopY, mPaint);
        }

        // Draw text
        textDisplayHeight = getHeight() - (float)(getPaddingBottom() / 3);
        textTopDisplayHeight = (float) getPaddingTop() / 2;
//        mDisplayText = String.valueOf(mProgress);

        canvas.drawText(mDisplayText, (float) getWidth() / 2, textDisplayHeight, mTextPaint);
        canvas.drawText(mDisplayTextTop, (float) getWidth() / 2, textTopDisplayHeight, mTextPaint);
    }

    public void setProgress(float progress, String displayText, String displayTopText){
        mProgress = progress;
        if(displayText != null) {
            mDisplayText = displayText;
        }else{
            mDisplayText = "";
        }

        if(displayTopText != null){
            mDisplayTextTop = displayTopText;
        }else{
            mDisplayTextTop = "";
        }
        invalidate();
    }
}
