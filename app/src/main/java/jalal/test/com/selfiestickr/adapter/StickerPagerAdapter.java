package jalal.test.com.selfiestickr.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.interf.OnStickerPagerItemClickListener;
import jalal.test.com.selfiestickr.model.StickerCategory;


public class StickerPagerAdapter extends PagerAdapter {

    private static final int NUM_STICKERS = 4;

    private final Context mContext;
    private OnStickerPagerItemClickListener mStickerClickListener;

    private StickerCategory mCategory;

    public StickerPagerAdapter(Context context) {
        mContext = context;
    }

    public void setOnStickerClickListener(OnStickerPagerItemClickListener listener) {
        mStickerClickListener = listener;
    }

    public void setCategory(StickerCategory category) {
        mCategory = category;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_sticker_pager, null);

        final int stickerId = mContext.getResources().getIdentifier(mCategory.id+"_"+position,
                "drawable", mContext.getPackageName());

        ImageView stickerImage = (ImageView) view.findViewById(R.id.stickerImage);
        stickerImage.setImageResource(stickerId);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStickerClickListener.onStickerPagerItemClick(stickerId);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return mCategory.numItems;
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
}