package jalal.test.com.selfiestickr.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.interf.OnStickerPagerItemClickListener;

/**
 * Created by jalals on 11/21/2014.
 */
public class StickerPagerAdapter extends PagerAdapter {

    private static final int NUM_STICKERS = 4;
    private final Context mContext;
    private OnStickerPagerItemClickListener mStickerClickLisneter;

    private final int[] mThumbs = {
        R.drawable.thumb_mexico_01, R.drawable.thumb_mexico_02, R.drawable.thumb_mexico_03,
        R.drawable.thumb_mexico_04, R.drawable.thumb_mexico_05, R.drawable.thumb_mexico_06,
        R.drawable.thumb_mexico_07, R.drawable.thumb_mexico_08, R.drawable.thumb_mexico_09,
        R.drawable.thumb_mexico_10, R.drawable.thumb_mexico_11, R.drawable.thumb_mexico_12,
        R.drawable.thumb_mexico_13
    };

    private final int[] mStickers = {
            R.drawable.mexico_01, R.drawable.mexico_02, R.drawable.mexico_03,
            R.drawable.mexico_04, R.drawable.mexico_05, R.drawable.mexico_06,
            R.drawable.mexico_07, R.drawable.mexico_08, R.drawable.mexico_09,
            R.drawable.mexico_10, R.drawable.mexico_11, R.drawable.mexico_12,
            R.drawable.mexico_13
    };

    public StickerPagerAdapter(Context context) {
        mContext = context;
    }

    public void setOnStickerClickListener(OnStickerPagerItemClickListener listener) {
        mStickerClickLisneter = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_sticker_pager, null);

        ImageView stickerImage = (ImageView) view.findViewById(R.id.sticker_image);
        stickerImage.setImageDrawable(mContext.getResources().getDrawable(mThumbs[position]));

        TextView stickerName = (TextView) view.findViewById(R.id.sticker_name);
        stickerName.setText(position+"");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStickerClickLisneter.onStickerPagerItemClick(position);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return mStickers.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getItemPosition (Object object) {
        return POSITION_NONE;
    }

    @Override
    public float getPageWidth(int position) {
        return 1.0f / NUM_STICKERS;
    }

    public int[] getStickers() {
        return mThumbs;
    }
}
