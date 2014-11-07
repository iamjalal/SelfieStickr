package jalal.test.com.selfiestickr.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Custom {@link android.view.View} class that responds to different gestures to transform
 * a {@link android.graphics.Bitmap}. The gestures are:
 *
 * - Pinch zoom: to scale up and down.
 * - One-finger move: to translate the image.
 * - Two-finger rotation: to rotate in the z-axis.
 * - Two-closed-fingers up/down move: to rotate in the x-axis.
 * - Two-closed-finger left/right move: to rotate in the y-axis.
 */
public class GestureTransformationView extends View {

    private static final int INVALID_POINTER_ID = -1;

    private static final float MAX_SCALE_FACTOR = 2.f;
    private static final float MIN_SCALE_FACTOR = .1f;

    private static final int MAX_X_AXIS_ROTATION = 180;
    private static final int MIN_X_AXIS_ROTATION = -180;

    private static final int ROTATE_SLOW_FACTOR = 3;

    private static final int POINTER_SPAN_THRESHOLD = 300;

    private int mActivePointerId = INVALID_POINTER_ID;
    private int mActivePointerId_ = INVALID_POINTER_ID;

    private float mLastTouchX, mLastTouchY;

    private float mInitTouchX, mInitTouchY;
    private float mInitTouchX_, mInitTouchY_;

    private Drawable mDrawable;
    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();

    private ScaleGestureDetector mScaleDetector;

    private float mPosX, mPosY;
    private float mRotationDegrees;
    private float mInitDegrees;
    private float mLastDegrees;

    private float mXAxisRotation;
    private float mYAxisRotation;
    private float mScaleFactor = 1.f;
    private boolean isScaling;

    public GestureTransformationView(Context context) {
        this(context, null, 0);
    }

    public GestureTransformationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureTransformationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setStickrDrawable(Drawable drawable) {
        mDrawable = drawable;
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth()/2, mDrawable.getIntrinsicHeight()/2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = mDrawable.getBounds().centerX();
        float centerY = mDrawable.getBounds().centerY();

        mCamera.save();
        mCamera.rotateX(mXAxisRotation);
        mCamera.rotateY(mYAxisRotation);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.concat(mMatrix);
        canvas.scale(mScaleFactor, mScaleFactor, centerX, centerY);
        canvas.rotate(mRotationDegrees, centerX, centerY);

        mDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: {

                final float x = ev.getX();
                final float y = ev.getY();

                mInitTouchX = x;
                mInitTouchY = y;

                mLastTouchX = mInitTouchX;
                mLastTouchY = mInitTouchY;

                mActivePointerId = ev.getPointerId(0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                //Info from first pointer
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                if(ev.getPointerCount() == 1) {
                    translate(dx, dy);
                }

                mLastTouchX = x;
                mLastTouchY = y;

                //Info from second pointer
                final int pointerIndex_ = ev.findPointerIndex(mActivePointerId_);
                if(pointerIndex_ == INVALID_POINTER_ID) {
                    break;
                }

                final float y_ = ev.getY(pointerIndex_);

                float pointerSpanY = Math.abs(y - y_);
                if(!isScaling && pointerSpanY < POINTER_SPAN_THRESHOLD) {
                    if (Math.abs(dy) > Math.abs(dx)) {
                        rotateXAxis(dy);
                    } else {
                        rotateYAxis(dx);
                    }
                }
                else {
                    rotate(ev);
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if(pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                else if(pointerId == mActivePointerId_) {
                    mActivePointerId_ = INVALID_POINTER_ID;
                }

                mLastDegrees = mRotationDegrees;
                isScaling = false;

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {

                final float x = ev.getX(1);
                final float y = ev.getY(1);

                mInitTouchX_ = x;
                mInitTouchY_ = y;

                mActivePointerId_ = ev.getPointerId(1);

                mInitDegrees = getDegrees(mInitTouchX, mInitTouchY, mInitTouchX_, mInitTouchY_);

                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            isScaling = false;
            if(detector.getCurrentSpanY() > POINTER_SPAN_THRESHOLD) {
                isScaling = true;
                mScaleFactor *= detector.getScaleFactor();
                mScaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(mScaleFactor, MAX_SCALE_FACTOR));
                invalidate();
            }

            return isScaling;
        }
    }

    private void rotate(MotionEvent ev) {

        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
        final int pointerIndex_ = ev.findPointerIndex(mActivePointerId_);
        if(pointerIndex_ != INVALID_POINTER_ID) {
            float currentDegrees = getDegrees(ev.getX(pointerIndex), ev.getY(pointerIndex),
                    ev.getX(pointerIndex_), ev.getY(pointerIndex_));
            mRotationDegrees = currentDegrees - mInitDegrees + mLastDegrees;
            invalidate();
        }
    }

    private void translate(float dx, float dy) {
        mPosX += dx;
        mPosY += dy;
        invalidate();
    }

    private void rotateXAxis(float dy) {
        mXAxisRotation -= dy / ROTATE_SLOW_FACTOR;
        mXAxisRotation = Math.max(MIN_X_AXIS_ROTATION,
                Math.min(mXAxisRotation, MAX_X_AXIS_ROTATION));
        invalidate();
    }

    private void rotateYAxis(float dx) {
        mYAxisRotation += dx / ROTATE_SLOW_FACTOR;
        mYAxisRotation = Math.max(MIN_X_AXIS_ROTATION,
                Math.min(mYAxisRotation, MAX_X_AXIS_ROTATION));
        invalidate();
    }

    private float getDegrees(float x1, float y1, float x2, float y2) {
        double deltaInit_x = x1 - x2;
        double deltaInit_y = y1 - y2;
        double radiansInit = Math.atan2(deltaInit_y, deltaInit_x);

        return (float) Math.toDegrees(radiansInit);
    }
}
