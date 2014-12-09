package jalal.test.com.selfiestickr.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.fragment.EditorFragment;
import jalal.test.com.selfiestickr.fragment.ImagePickerFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!handleIntent(getIntent()) && savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, ImagePickerFragment.newInstance())
                    .commit();
        }
    }

    private boolean handleIntent(Intent intent) {

        if(intent == null) {
            return false;
        }

        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, EditorFragment.newInstance(imageUri))
                        .commit();
                return true;
            }
        }

        return false;
    }
}
