package ru.korotkov.timefixer.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import ru.korotkov.timefixer.R;

/**
 * SeekArc.java
 * <p>
 * This is a class that functions much like a SeekBar but
 * follows a circle path instead of a straight line.
 *
 * @author Neil Davies
 */
public class SeekArc extends View {

    private static final String TAG = SeekArc.class.getSimpleName();
    private static int INVALID_PROGRESS_VALUE = -1;
    // The initial rotational offset -90 means we start at 12 o'clock
    private final int mAngleOffset = -90;

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private int mMax = 100;

    /**
     * The Current value that the SeekArc is set to
     */
    private int mProgress = 0;

    /**
     * The width of the progress line for this SeekArc
     */
    private int mProgressWidth = 4;

    /**
     * The Width of the background arc for the SeekArc
     */
    private int mArcWidth = 2;

    /**
     * The Angle to start drawing this Arc from
     */
    private int mStartAngle = 0;

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    private int mSweepAngle = 360;

    /**
     * The rotation of the SeekArc- 0 is twelve o'clock
     */
    private int mRotation = 0;

    /**
     * Give the SeekArc rounded edges
     */
    private boolean mRoundedEdges = false;

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private boolean mClockwise = true;

    // Internal variables
    private int mArcRadius = 0;
    private float mProgressSweep = 0;
    private RectF mArcRect = new RectF();
    private Paint mArcPaint;
    private Paint mProgressPaint;
    private OnSeekArcChangeListener mOnSeekArcChangeListener;

    public interface OnSeekArcChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param seekArc  The SeekArc whose progress has changed
         * @param progress The current progress level. This will be in the range
         *                 0..max where max was set by
         *                 {@link SeekArc#setMax(int)}. (The default value for
         *                 max is 100.)
         */
        void onProgressChanged(SeekArc seekArc, int progress);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStartTrackingTouch(SeekArc seekArc);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the seekarc.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStopTrackingTouch(SeekArc seekArc);
    }

    public SeekArc(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SeekArc(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.seekArcStyle);
    }

    public SeekArc(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        Log.d(TAG, "Initialising SeekArc");
        final Resources res = getResources();
        float density = context.getResources().getDisplayMetrics().density;

        // Convert progress width to pixels for current density
        mProgressWidth = (int) (mProgressWidth * density);

        // Attribute initialization
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekArc, defStyle, 0);

        mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
        mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
        mProgressWidth = (int) a.getDimension(R.styleable.SeekArc_progressWidth, mProgressWidth);
        mArcWidth = (int) a.getDimension(R.styleable.SeekArc_arcWidth, mArcWidth);
        mStartAngle = a.getInt(R.styleable.SeekArc_startAngle, mStartAngle);
        mSweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, mSweepAngle);
        mRotation = a.getInt(R.styleable.SeekArc_rotation, mRotation);
        mRoundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges, mRoundedEdges);
        mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise, mClockwise);

        a.recycle();


        mProgress = Math.min(mProgress, mMax);
        mProgress = Math.max(mProgress, 0);

        mSweepAngle = Math.min(mSweepAngle, 360);
        mSweepAngle = Math.max(mSweepAngle, 0);

        mProgressSweep = (float) mProgress / mMax * mSweepAngle;

        mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
        mStartAngle = Math.max(mStartAngle, 0);

        mArcPaint = new Paint();
        mArcPaint.setColor(Color.GRAY);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mClockwise) {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }

        // Draw the arcs
        final int arcStart = mStartAngle + mAngleOffset + mRotation;
        final int arcSweep = mSweepAngle;
        canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);
        canvas.drawArc(mArcRect, arcStart, mProgressSweep, false,
                mProgressPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        float top;
        float left;
        int arcDiameter;

        arcDiameter = min - getPaddingLeft();
        mArcRadius = arcDiameter / 2;
        top = height / 2 - (arcDiameter / 2);
        left = width / 2 - (arcDiameter / 2);
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private void updateProgress(int progress) {
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        progress = Math.min(progress, mMax);
        progress = Math.max(progress, 0);
        mProgress = progress;

        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onProgressChanged(this, progress);
        }
        mProgressSweep = (float) progress / mMax * mSweepAngle;
        invalidate();
    }

    /**
     * Sets a listener to receive notifications of changes to the SeekArc's
     * progress level. Also provides notifications of when the user starts and
     * stops a touch gesture within the SeekArc.
     *
     * @param l The seek bar notification listener
     * @see OnSeekArcChangeListener
     */
    public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
        mOnSeekArcChangeListener = l;
    }

    public void setProgress(int progress) {
        setProgress(progress, false);
    }

    public void setProgress(int progress, boolean animated) {
        setProgress(progress, animated, 300);
    }

    public void setProgress(int progress, boolean animated, long duration) {
        if (animated) {
            SeekArcAnimation animation = new SeekArcAnimation(this, mProgress, progress);
            animation.setDuration(duration);
            startAnimation(animation);
        } else {
            updateProgress(progress);
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public int getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(int mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(int mArcWidth) {
        this.mArcWidth = mArcWidth;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    public int getArcRotation() {
        return mRotation;
    }

    public void setArcRotation(int mRotation) {
        this.mRotation = mRotation;
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int mSweepAngle) {
        this.mSweepAngle = mSweepAngle;
    }

    public void setRoundedEdges(boolean isEnabled) {
        mRoundedEdges = isEnabled;
        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
    }

    public void setClockwise(boolean isClockwise) {
        mClockwise = isClockwise;
    }

    public boolean isClockwise() {
        return mClockwise;
    }

    public int getProgressColor() {
        return mProgressPaint.getColor();
    }

    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    public int getArcColor() {
        return mArcPaint.getColor();
    }

    public void setArcColor(int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    public static class SeekArcAnimation extends Animation {
        private SeekArc seekArc;
        private float from;
        private float to;

        public SeekArcAnimation(SeekArc seekArc, float from, float to) {
            super();
            this.seekArc = seekArc;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            seekArc.setProgress((int) value);
        }
    }
}
