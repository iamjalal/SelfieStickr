package jalal.test.com.selfiestickr.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.adapter.CategoryPagerAdapter;
import jalal.test.com.selfiestickr.adapter.StickerPagerAdapter;
import jalal.test.com.selfiestickr.interf.OnCategorySelectListener;
import jalal.test.com.selfiestickr.interf.OnStickerMoveListener;
import jalal.test.com.selfiestickr.interf.OnStickerPagerItemClickListener;
import jalal.test.com.selfiestickr.model.StickerCategory;
import jalal.test.com.selfiestickr.util.FileUtils;
import jalal.test.com.selfiestickr.util.MyMediaConnectionClient;
import jalal.test.com.selfiestickr.view.GestureTransformationView;
import jalal.test.com.selfiestickr.view.ScreenSizeAwareImageView;

/**
 * Fragment in charge of all the image edition and overlay selection
 */
public class EditorFragment extends Fragment implements OnStickerPagerItemClickListener,
        OnStickerMoveListener, OnCategorySelectListener {

    private static final String BUNDLE_IMAGE_DATA = "imageUri";
    private static final String BUNDLE_IS_EDITING = "isEditing";
    private static final String BUNDLE_IS_SAVED = "isSaved";

    private static final int BACKGROUND_COLOR_ANIM_DURATION = 500;

    private List<GestureTransformationView> mStickersList =
            new ArrayList<GestureTransformationView>();

    private ScreenSizeAwareImageView mImageView;
    private FrameLayout mContainer;
    private ViewPager mStickerPager;

    StickerPagerAdapter mStickerPagerAdapter;
    CategoryPagerAdapter mCategoryPagerAdapter;

    private Uri mUri;
    private Uri mFileUri;

    private boolean mIsEditing;
    private boolean mIsSaved;

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
            mIsEditing = savedInstanceState.getBoolean(BUNDLE_IS_EDITING);
            mIsSaved = savedInstanceState.getBoolean(BUNDLE_IS_SAVED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        mContainer = (FrameLayout)view.findViewById(R.id.container);

        mStickerPagerAdapter = new StickerPagerAdapter(getActivity());
        mStickerPagerAdapter.setOnStickerClickListener(this);
        mStickerPager = (ViewPager)view.findViewById(R.id.stickerPager);

        mCategoryPagerAdapter = new CategoryPagerAdapter(getActivity());
        mCategoryPagerAdapter.setCategorySelectionListener(this);
        ViewPager categoryPager = (ViewPager)view.findViewById(R.id.categoryPager);
        categoryPager.setAdapter(mCategoryPagerAdapter);

        mImageView = new ScreenSizeAwareImageView(getActivity());
        mImageView.setImageURI(mUri);
        mContainer.addView(mImageView);

        setPaletteBackground(mImageView);

        Button saveButton = (Button)view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveImage();
                    }
                }).start();
            }
        });

        Button okButton = (Button)view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEditing = false;
            }
        });

        Button undoButton = (Button)view.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        Button shareButton = (Button)view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveImage();
                        share();
                    }
                }).start();
            }
        });

        onCategorySelected(0);

        return view;
    }

    @Override
    public void onStickerPagerItemClick(int id) {

        GestureTransformationView sticker = null;

        if(mIsEditing) {
            int size = mStickersList.size();
            sticker = mStickersList.get(size - 1);
        }
        else {
            sticker = new GestureTransformationView(getActivity());
            sticker.addOnStickerMoveListener(this);
            mStickersList.add(sticker);
            mContainer.addView(sticker);
        }

        Drawable stickerDrawable = getResources().getDrawable(id);
        sticker.setStickrDrawable(stickerDrawable);
    }

    @Override
    public void onStickerMove() {
        mIsSaved = false;
        mIsEditing = true;
    }

    private void saveImage() {

        if(mIsSaved) {
            return;
        }

        Bitmap mergedBitmap = getMergedBitmap();
        FileOutputStream out = null;
        try {
            File file = FileUtils.getFile(getActivity());
            mFileUri = Uri.fromFile(file);
            out = new FileOutputStream(file);
            mergedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    requestMediaScan(mFileUri);
                    mIsSaved = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getMergedBitmap() {
        Bitmap imageOriginalBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        int imageScreenHeight = mImageView.getDrawableScreenHeight();
        int imageScreenWidth = mImageView.getDrawableScreenWidth();

        Bitmap totalBitmap = Bitmap.createScaledBitmap(imageOriginalBitmap, imageScreenWidth,
                imageScreenHeight, false);

        Canvas canvas = new Canvas(totalBitmap);
        addStickersToCanvas(canvas, totalBitmap);

        return totalBitmap;
    }

    private void addStickersToCanvas(Canvas canvas, Bitmap bitmap) {
        for(GestureTransformationView sticker : mStickersList) {
            sticker.buildDrawingCache(true);

            int top = (mImageView.getHeight() - bitmap.getHeight()) / 2;
            int left = (mImageView.getWidth() - bitmap.getWidth()) / 2;
            Bitmap stickerBitmap = Bitmap.createBitmap(sticker.getDrawingCache(true),
                    left, top, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(stickerBitmap, 0, 0, null);
            sticker.destroyDrawingCache();
        }
    }

    private void undo() {
        if(!mStickersList.isEmpty()) {
            int size = mStickersList.size();
            GestureTransformationView sticker = mStickersList.get(size - 1);
            mContainer.removeView(sticker);
            mStickersList.remove(sticker);
            mIsEditing = false;
        }
    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, mFileUri);
        startActivity(Intent.createChooser(share, "Share image"));
    }

    private void setPaletteBackground(final ImageView image) {

        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                int colorFrom = getResources().getColor(android.R.color.darker_gray);
                int colorTo = palette.getDarkMutedColor(android.R.color.darker_gray);
                final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(BACKGROUND_COLOR_ANIM_DURATION);
                colorAnimation.setInterpolator(new AccelerateInterpolator());
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        image.setBackgroundColor((Integer) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_IMAGE_DATA, mUri);
        outState.putBoolean(BUNDLE_IS_EDITING, mIsEditing);
        outState.putBoolean(BUNDLE_IS_SAVED, mIsSaved);
    }

    @Override
    public void onCategorySelected(int position) {
        StickerCategory category = mCategoryPagerAdapter.getCategories()[position];
        mStickerPagerAdapter.setCategory(category);
        mStickerPager.setAdapter(mStickerPagerAdapter);
    }

    public void requestMediaScan(Uri uri) {
        MyMediaConnectionClient client = new MyMediaConnectionClient(uri.getPath());
        MediaScannerConnection connection = new MediaScannerConnection(getActivity(), client);
        client.setConnection(connection);
        connection.connect();
    }
}