package jalal.test.com.selfiestickr.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jalal.test.com.selfiestickr.R;

/**
 * Created by jalals on 12/1/2014.
 */
public class FileUtils {

    public static File getFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) +
                "/" + context.getResources().getString(R.string.storage_directory)+ "/");
        if(!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}