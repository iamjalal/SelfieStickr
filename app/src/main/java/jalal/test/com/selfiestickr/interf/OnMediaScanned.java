package jalal.test.com.selfiestickr.interf;

import android.net.Uri;

public interface OnMediaScanned {

    /**
     * Notifies that a media item has been scanned
     * @param contentUri
     */
    public void mediaScanned(Uri contentUri);
}