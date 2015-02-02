package jalal.test.com.selfiestickr.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by jalals on 2/2/2015.
 */
public class DimensionUtils {

    public static final float ONE_FINGER_DIMENSION_MM = 10;

    public static float pixelsToMilimeters(Context context, float pixels) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1,
                context.getResources().getDisplayMetrics());
        return pixels / px;
    }
}
