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

    private static final int NUM_STICKERS = 2;
    private final Context mContext;
    private OnStickerPagerItemClickListener mStickerClickLisneter;

    private final int[] mStickers = {
        R.drawable.sample_stick_1, R.drawable.sample_stick_2,
        R.drawable.sample_stick_1, R.drawable.sample_stick_2
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
        stickerImage.setImageDrawable(mContext.getResources().getDrawable(mStickers[position]));

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
        return mStickers;
    }
}
