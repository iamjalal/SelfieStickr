package jalal.test.com.selfiestickr.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.view.GestureTransformationView;

/**
 * Fragment in charge of all the image edition and overlay selection
 */
public class EditorFragment extends Fragment {

    public static EditorFragment newInstance() {
        EditorFragment fragment = new EditorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        GestureTransformationView stickerContainer = (GestureTransformationView)view.findViewById(R.id.stickerContainer);
        stickerContainer.setStickrDrawable(getResources().getDrawable(R.drawable.sample_stick_2));
        return  view;
    }
}
