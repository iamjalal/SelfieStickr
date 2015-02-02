package jalal.test.com.selfiestickr.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.util.FileUtils;


public class ImagePickerFragment extends Fragment {

    public static final String TAG = "SelfieStickr";

    private static final String SAVED_IMAGE_URI = "savedImageUri";

    public static final int RESULT_GALLERY = 100;
    public static final int RESULT_CAMERA = 200;

    private Uri mCaptureImageUri;

    public static ImagePickerFragment newInstance() {
        return new ImagePickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mCaptureImageUri = savedInstanceState.getParcelable(SAVED_IMAGE_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_picker, container, false);

        Button galleryButton = (Button)view.findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(galleryIntent, RESULT_GALLERY);
                }
            }
        });

        Button cameraButton = (Button)view.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = FileUtils.getFile(getActivity());
                    } catch (IOException e) {
                        Log.e(TAG, "Could not create file");
                    }

                    if (photoFile != null) {
                        mCaptureImageUri = Uri.fromFile(photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureImageUri);
                        startActivityForResult(cameraIntent, RESULT_CAMERA);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == RESULT_GALLERY) {
            startImageEditor(data.getData());
        }
        else if(requestCode == RESULT_CAMERA) {
            MediaScannerConnection.scanFile(getActivity(), new String[]{mCaptureImageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("Scanned = ", "Scanned " + path);
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    startImageEditor(getImageUri());
                                }
                            });
                        }
                    });
        }
    }

    private Uri getImageUri() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imageCursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");

        imageCursor.moveToPosition(0);
        int dataColumnIndex = imageCursor
                .getColumnIndex(MediaStore.Images.Media.DATA);
        String path = imageCursor.getString(dataColumnIndex);
        imageCursor.close();

        return Uri.parse(path);
    }

    private void startImageEditor(Uri uri) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, EditorFragment.newInstance(uri))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_IMAGE_URI, mCaptureImageUri);
    }
}
