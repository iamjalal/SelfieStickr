package jalal.test.com.selfiestickr.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jalal.test.com.selfiestickr.R;


public class ImagePickerFragment extends Fragment {

    public static final int RESULT_LOAD_IMAGE = 0;

    public static ImagePickerFragment newInstance() {
        return new ImagePickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_picker, container, false);

        Button pickButtom = (Button)view.findViewById(R.id.pickButton);
        pickButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                getActivity().startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        return view;
    }
}
