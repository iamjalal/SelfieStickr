package jalal.test.com.selfiestickr.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by jalals on 12/18/2014.
 */
public class ScreenSizeAwareImageView extends ImageView {

    private int mActualWidth;
    private int mActualHeight;

    public ScreenSizeAwareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        // Get image matrix values and place them in an array
        float[] values = new float[9];
        getImageMatrix().getValues(values);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = values[Matrix.MSCALE_X];
        final float scaleY = values[Matrix.MSCALE_Y];

        Log.v("STICKR", "Sx: "+scaleX+" Sy: "+scaleY);

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        mActualWidth = Math.round(origW * scaleX);
        mActualHeight = Math.round(origH * scaleY);

        Log.v("STICKR", "w: "+mActualWidth+" h: "+mActualHeight);
    }

    public int getScreenWidth() {
        return mActualWidth;
    }

    public int getScreenHeight() {
        return mActualHeight;
    }
}
