package jalal.test.com.selfiestickr.util;

import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * This is used to scan a given file (image in this case) to ensure it is added
 * to the media store thus making its content uri available. It returns the
 * content form Uri by means of a callback on the supplied handler's thread.
 */
public class MyMediaConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {

    private final String mPath;
    private MediaScannerConnection mConnection;

    public MyMediaConnectionClient(String path) {
        mPath = path;
    }

    @Override
    public void onMediaScannerConnected() {
        //TODO: There is a bug in kitkat in the media store scan process
        //https://code.google.com/p/android/issues/detail?id=68056
        //This can be worked around as outlined in the link
        mConnection.scanFile(mPath, null);
    }

    @Override
    public void onScanCompleted(final String path, final Uri uri) {
        mConnection.disconnect();
    }

    public void setConnection(MediaScannerConnection connection) {
        mConnection = connection;
    }
}