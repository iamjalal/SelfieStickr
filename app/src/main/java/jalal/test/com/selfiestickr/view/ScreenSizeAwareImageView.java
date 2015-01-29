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
        super(context);
    }

    public ScreenSizeAwareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        final Drawable d = getDrawable();
        if(d == null) {
            return;
        }

        float[] values = new float[9];
        getImageMatrix().getValues(values);

        final float scaleX = values[Matrix.MSCALE_X];
        final float scaleY = values[Matrix.MSCALE_Y];

        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        mActualWidth = Math.round(origW * scaleX);
        mActualHeight = Math.round(origH * scaleY);
    }

    public int getDrawableScreenWidth() {
        return mActualWidth;
    }

    public int getDrawableScreenHeight() {
        return mActualHeight;
    }
}
