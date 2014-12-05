package jalal.test.com.selfiestickr.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.adapter.StickerPagerAdapter;
import jalal.test.com.selfiestickr.interf.OnStickerPagerItemClickListener;
import jalal.test.com.selfiestickr.util.FileUtils;
import jalal.test.com.selfiestickr.view.GestureTransformationView;

/**
 * Fragment in charge of all the image edition and overlay selection
 */
public class EditorFragment extends Fragment implements OnStickerPagerItemClickListener {

    private static final String BUNDLE_IMAGE_DATA = "imageUri";
    private static final String BUNDLE_IS_DONE = "isDoneEditing";

    private static final int BACKGROUND_COLOR_ANIM_DURATION = 650;

    private List<GestureTransformationView> mStickersList =
            new ArrayList<GestureTransformationView>();

    ImageView mImageView;
    FrameLayout mContainer;
    StickerPagerAdapter mStickerPagerAdapter;

    private Uri mUri;
    private boolean mIsDoneEditing = true;

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
            mIsDoneEditing = savedInstanceState.getBoolean(BUNDLE_IS_DONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        mContainer = (FrameLayout)view.findViewById(R.id.container);

        mStickerPagerAdapter = new StickerPagerAdapter(getActivity());
        mStickerPagerAdapter.setOnStickerClickListener(this);

        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(mStickerPagerAdapter);

        mImageView = (ImageView)view.findViewById(R.id.image);
        mImageView.setImageURI(mUri);
        setPaletteBackground(mImageView);

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

        Button okButton = (Button)view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsDoneEditing = true;
            }
        });

        Button undoButton = (Button)view.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        return  view;
    }

    @Override
    public void onStickerPagerItemClick(int position) {

        if(mIsDoneEditing) {
            addNewSticker();
            mIsDoneEditing = false;
        }

        int stickerId = mStickerPagerAdapter.getStickers()[position];

        if(!mStickersList.isEmpty()) {
            GestureTransformationView sticker = mStickersList.get(mStickersList.size() - 1);
            sticker.setStickrDrawable(getResources().getDrawable(stickerId));
        }
    }

    private void addNewSticker() {
        GestureTransformationView newSticker = new GestureTransformationView(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        newSticker.setLayoutParams(params);

        mStickersList.add(newSticker);
        mContainer.addView(newSticker);
    }

    private void saveImage() {
        Bitmap imageBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        Bitmap bmOverlay = Bitmap.createBitmap(imageBitmap.getWidth(),
                imageBitmap.getHeight(), imageBitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(imageBitmap, new Matrix(), null);
        addStickersToCanvas(canvas, imageBitmap.getWidth(), imageBitmap.getHeight());

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

    private void addStickersToCanvas(Canvas canvas, int width, int height) {
        for(GestureTransformationView sticker : mStickersList) {
            sticker.buildDrawingCache(true);
            Bitmap stickerBitmap = Bitmap.createScaledBitmap(sticker.getDrawingCache(true),
                    width, height, false);
            sticker.destroyDrawingCache();

            canvas.drawBitmap(stickerBitmap, 0, 0, null);
        }
    }

    private void undo() {
        if(!mStickersList.isEmpty()) {
            GestureTransformationView sticker = mStickersList.get(mStickersList.size() - 1);
            mContainer.removeView(sticker);
            mStickersList.remove(sticker);
            mIsDoneEditing = true;
        }
    }

    private void setPaletteBackground(final ImageView image) {

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                int colorFrom = getResources().getColor(android.R.color.darker_gray);
                int colorTo = palette.getMutedColor(android.R.color.darker_gray);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(BACKGROUND_COLOR_ANIM_DURATION);
                colorAnimation.setInterpolator(new AccelerateInterpolator());
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        image.setBackgroundColor((Integer)animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_IMAGE_DATA, mUri);
        outState.putBoolean(BUNDLE_IS_DONE, mIsDoneEditing);
    }
}
