package sammyt.deviceoverview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by Sammy on 7/3/2017.
 */

public class BubbleChartView extends View{

    private final String LOG_TAG = this.getClass().getSimpleName();

    private Paint mTextPaint;
    private Paint mGraphTextPaint;
    private Paint mGraphPaint;
    private Paint mGraphLinePaint;
    private Paint mPaint;

    private float mTextSize = 0;

    private float mViewWidth = 0;
    private float mViewHeight = 0;
    private float mUsableWidth = 0;
    private float mUsableHeight = 0;
    private float mGraphWidth = 0;
    private float mGraphHeight = 0;
    private float mRotatedTextStart = 0;

    private Boolean mShowHorizontalLines = true;
    private Boolean mShowVerticalLines = true;

    private String xAxisLabel = "About Us";
    private String yAxisLabel = "Something";

    private float x = 0;
    private float y = 0;
    private float z = 2;

    private float minX = -12;
    private float minY = -10;
    private float minZ = -10;

    private float maxX = 10;
    private float maxY = 10;
    private float maxZ = 10;

    private DecimalFormat df = new DecimalFormat("#.#");

    public BubbleChartView(Context context){
        super(context);
        init();
    }

    public BubbleChartView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        int baseColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1f, getResources().getDisplayMetrics());
        float lineStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                0.2f, getResources().getDisplayMetrics());

        // Text Paint
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(baseColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f,
                getResources().getDisplayMetrics());
        mTextPaint.setTextSize(mTextSize);

        mGraphTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraphTextPaint.setColor(baseColor);
        mGraphTextPaint.setTextAlign(Paint.Align.CENTER);
        float graphTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9.5f,
                getResources().getDisplayMetrics());
        mGraphTextPaint.setTextSize(graphTextSize);

        // Graph Paint
        mGraphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraphPaint.setColor(baseColor);
        mGraphPaint.setStyle(Paint.Style.STROKE);
        mGraphPaint.setStrokeWidth(strokeWidth);
        mGraphPaint.setStrokeCap(Paint.Cap.ROUND);

        mGraphLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraphLinePaint.setColor(baseColor);
        mGraphLinePaint.setStyle(Paint.Style.STROKE);
        mGraphLinePaint.setStrokeWidth(lineStrokeWidth);
        mGraphLinePaint.setStrokeCap(Paint.Cap.ROUND);

        // Paint (Value/Circle paint)
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        //// TODO: Set style?
        //// TODO: Set stroke width?
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 0);

        int minHeight = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int height = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

        mViewWidth = MeasureSpec.getSize(width);
        mViewHeight = MeasureSpec.getSize(height);
        mUsableWidth = mViewWidth - (getPaddingLeft() + getPaddingRight());
        mUsableHeight = mViewHeight - (getPaddingTop() + getPaddingBottom());
        //// TODO: Graph w & h

        mRotatedTextStart = getPaddingLeft() + mTextSize / /*1.5f*/1.2f;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // Draw the y axis label
        canvas.save();
//        canvas.rotate(-90f, getPaddingLeft(), getHeight()/2);
        canvas.rotate(-90f, mRotatedTextStart, getHeight()/2);
        canvas.drawText(yAxisLabel, mRotatedTextStart, getHeight()/2, mTextPaint);
        canvas.restore();

        // Draw x axis label
        canvas.drawText(xAxisLabel, getWidth() / 2, getHeight() - getPaddingBottom()
                - (mTextSize/18f), mTextPaint);

        //// TODO: Draw graph numbers
//        canvas.drawText("10", getPaddingLeft() + mTextSize*1.5f, getHeight()/2, mTextPaint);
//        canvas.drawText("10", getWidth() / 2, getHeight() - getPaddingBottom() - mTextSize*1.2f,
//                mTextPaint);

        float lineX = getPaddingLeft() + mTextSize*2.4f + mGraphPaint.getStrokeWidth();
        float lineY = getHeight() - getPaddingBottom() - mTextSize*2.3f + mGraphPaint.getStrokeWidth();

        canvas.drawLine(lineX, getPaddingTop(), lineX, lineY, mGraphPaint); // y axis
        canvas.drawLine(lineX, lineY, getWidth() - getPaddingRight(), lineY, mGraphPaint); // x axis

        ////
        //// TODO: Remove
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;
        Log.d(LOG_TAG, "disp X: " + displayWidth/getWidth() +  " Y: " + displayHeight/getHeight());

        float density = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f,
                getResources().getDisplayMetrics());
        float scaleY = lineY/(maxY - minY); //// TODO: Remove
//        Log.d(LOG_TAG, "scaleY: " +scaleY + " lineY: " + lineY + " " + getHeight());
        Log.d(LOG_TAG, "" + lineY / scaleY + " yDensity: " +density);
        Log.d(LOG_TAG, "yPlots " + lineY / density + " lineY: " + lineY + " " + getHeight());

        // Horizontal lines
        float plots = lineY / density;
        float valInterval = (maxY - minY) / plots;
        float startVal = minY;
        for(int i=0; i < plots; i++){
//            canvas.drawPoint(lineX + 10, lineY - (i * density) /*+ getPaddingTop()*/, mGraphPaint);
            if(i != 0 && mShowHorizontalLines) {
                canvas.drawLine(lineX, lineY - (i * density), getWidth() - getPaddingRight(),
                        lineY - (i * density), mGraphLinePaint);
            }
//            canvas.drawPoint(lineX + 20, i * scaleY + getPaddingTop(), mGraphPaint);
            String val = String.valueOf(df.format(startVal  + (valInterval * i)));
            canvas.drawText(val, getPaddingLeft() + mTextSize*1.8f, lineY - (i * density) +
                    (mGraphTextPaint.getTextSize()/3), mGraphTextPaint);
        }

        // Vertical lines
        float xDensity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f,
                getResources().getDisplayMetrics());
        float xPlots = (getWidth() - lineX) / xDensity;
        float xValInterval = (maxX - minX) / xPlots;
        float xStartval = minX;
        Log.d(LOG_TAG, "xDensity: " + xDensity);
        Log.d(LOG_TAG, "xPlots: " + xPlots + " lineX: " + lineX + " " + getWidth());
        for(int i=0; i < xPlots; i++){
            if(i != 0 && mShowVerticalLines){
                canvas.drawLine(lineX + (i * xDensity), lineY, lineX + (i * xDensity),
                        getPaddingTop(), mGraphLinePaint);
            }

            String val = String.valueOf(df.format(xStartval  + (xValInterval * i)));
            canvas.drawText(val, lineX + (i * xDensity), getHeight() - getPaddingBottom()
                            - /*mGraphTextPaint.getTextSize()*1.2f*/
                            mTextSize*1.2f, mGraphTextPaint);
        }
        ////


        //// TODO: Draw point (x,y,z)
        //// TODO: Fix plot in negative (find 0 x & y axis)
        float xOffset = 0;
        float yOffset = 0;
        if(minX < 0){
//            xOffset = minX * -xDensity;
            xOffset = (xDensity * xPlots)/((maxX - minX)/minX); //// TODO: this is wrong
        }
        if(minY < 0){
            yOffset = minY * -yOffset;
        }
        Log.d(LOG_TAG, "xOff " + xOffset + " xPlots: " + xPlots);
//        canvas.drawCircle(cx, cy, radius, paint);
//        float circleX = getPaddingLeft() + xDensity + x;
        float circleX = lineX + (xDensity * x) - xOffset;
//        float circleY = getHeight() - getPaddingBottom() - mTextSize*1.2f - y;
        float circleY = lineY - (density * y);
        float circleZ = 10f + (5f * z);
        canvas.drawCircle(circleX, circleY, circleZ, mPaint);
    }
}
