package jalal.test.com.selfiestickr.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jalals on 12/18/2014.
 */
public class ScreenSizeAwareImageView extends ImageView {

    private int mActualWidth;
    private int mActualHeight;

    public ScreenSizeAwareImageView(Context context) {
        this(context, null, 0);
    }

    public ScreenSizeAwareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScreenSizeAwareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        mActualWidth = Math.round(origW * scaleX);
        mActualHeight = Math.round(origH * scaleY);
    }

    public int getScreenWidth() {
        return mActualWidth;
    }

    public int getScreenHeight() {
        return mActualHeight;
    }
}
