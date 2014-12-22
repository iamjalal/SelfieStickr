package jalal.test.com.selfiestickr.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jalal.test.com.selfiestickr.R;
import jalal.test.com.selfiestickr.interf.OnCategorySelectListener;
import jalal.test.com.selfiestickr.model.StickerCategory;

/**
 * Created by jalals on 12/10/2014.
 */
public class CategoryPagerAdapter extends PagerAdapter {

    private static final int NUM_CATEGORIES = 2;

    private final Context mContext;
    private OnCategorySelectListener mCategorySelectListener;

    private final StickerCategory[] mCategories = {
        new StickerCategory("mexico", 14),
        new StickerCategory("comic", 15),
        new StickerCategory("mexico", 14),
        new StickerCategory("comic", 13)
    };

    public CategoryPagerAdapter(Context context) {
        mContext = context;
    }

    public void setCategorySelectionListener(OnCategorySelectListener listener) {
        mCategorySelectListener = listener;
    }

    @Override
    public int getCount() {
        return mCategories.length;
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
        return 1.0f / NUM_CATEGORIES;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_category_pager, null);

        StickerCategory category = mCategories[position];

        TextView stickerName = (TextView) view.findViewById(R.id.categoryName);
        stickerName.setText(category.id);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategorySelectListener.onCategorySelected(position);
            }
        });

        container.addView(view);

        return view;
    }

    public StickerCategory[] getCategories() {
        return mCategories;
    }
}
