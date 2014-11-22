package jalal.test.com.selfiestickr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.fragment.EditorFragment;
import jalal.test.com.selfiestickr.fragment.ImagePickerFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, ImagePickerFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImagePickerFragment.RESULT_LOAD_IMAGE
                && resultCode == Activity.RESULT_OK) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, EditorFragment.newInstance(data.getData()))
                    .commit();
        }
    }
}
