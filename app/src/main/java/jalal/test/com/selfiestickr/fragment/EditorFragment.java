package jalal.test.com.selfiestickr.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.adapter.StickerPagerAdapter;
import jalal.test.com.selfiestickr.interf.OnStickerPagerItemClickListener;
import jalal.test.com.selfiestickr.util.FileUtils;
import jalal.test.com.selfiestickr.view.GestureTransformationView;

/**
 * Fragment in charge of all the image edition and overlay selection
 */
public class EditorFragment extends Fragment implements OnStickerPagerItemClickListener {

    GestureTransformationView mStickerContainer;
    ImageView mImageView;

    StickerPagerAdapter mStickerPagerAdapter;

    private static final String BUNDLE_IMAGE_DATA = "image_uri";

    private Uri mUri;

    public static EditorFragment newInstance(Uri data) {
        EditorFragment fragment = new EditorFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_IMAGE_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            mUri = args.getParcelable(BUNDLE_IMAGE_DATA);
        }
        else if(savedInstanceState != null) {
            mUri = savedInstanceState.getParcelable(BUNDLE_IMAGE_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        mStickerContainer = (GestureTransformationView)view.findViewById(R.id.stickerContainer);

        mStickerPagerAdapter = new StickerPagerAdapter(getActivity());
        mStickerPagerAdapter.setOnStickerClickListener(this);

        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(mStickerPagerAdapter);

        mImageView = (ImageView)view.findViewById(R.id.image);
        mImageView.setImageURI(mUri);

        Button saveButton = (Button)view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Saving...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveImage();
                    }
                }).start();
            }
        });

        return  view;
    }

    @Override
    public void onStickerPagerItemClick(int position) {
        int stickerId = mStickerPagerAdapter.getStickers()[position];
        mStickerContainer.setStickrDrawable(getResources().getDrawable(stickerId));
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_IMAGE_DATA, mUri);
    }

    private void saveImage() {
        Bitmap imageBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        mStickerContainer.buildDrawingCache(true);
        Bitmap stickerBitmap = Bitmap.createScaledBitmap(mStickerContainer.getDrawingCache(true),
                imageBitmap.getWidth(), imageBitmap.getHeight(), false);
        mStickerContainer.destroyDrawingCache();

        Bitmap bmOverlay = Bitmap.createBitmap(imageBitmap.getWidth(),
                imageBitmap.getHeight(), imageBitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(imageBitmap, new Matrix(), null);
        canvas.drawBitmap(stickerBitmap, 0, 0, null);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FileUtils.getFile(getActivity()));
            bmOverlay.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
