package jalal.test.com.selfiestickr.activity;

import android.app.Activity;
import android.os.Bundle;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.fragment.EditorFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, EditorFragment.newInstance())
                    .commit();
        }
    }
}