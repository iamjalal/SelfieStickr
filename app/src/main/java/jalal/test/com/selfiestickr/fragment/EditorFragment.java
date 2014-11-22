package jalal.test.com.selfiestickr.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.adapter.StickerPagerAdapter;
import jalal.test.com.selfiestickr.interf.OnStickerPagerItemClickListener;
import jalal.test.com.selfiestickr.view.GestureTransformationView;

/**
 * Fragment in charge of all the image edition and overlay selection
 */
public class EditorFragment extends Fragment implements OnStickerPagerItemClickListener {

    GestureTransformationView mStickerContainer;
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

        ImageView imageView = (ImageView)view.findViewById(R.id.image);
        imageView.setImageURI(mUri);

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
}
