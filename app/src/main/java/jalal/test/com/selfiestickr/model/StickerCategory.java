package jalal.test.com.selfiestickr.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jalals on 12/10/2014.
 */
public class StickerCategory implements Parcelable{

    public final String id;
    public final int numItems;

    public StickerCategory(String id, int numItems) {
        this.id = id;
        this.numItems = numItems;
    }

    private StickerCategory(Parcel in) {
        id = in.readString();
        numItems = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(numItems);
    }

    public static final Creator<StickerCategory> CREATOR = new Creator<StickerCategory>() {

        @Override
        public StickerCategory createFromParcel(Parcel in) {
            return new StickerCategory(in);
        }

        @Override
        public StickerCategory[] newArray(int size) {
            return new StickerCategory[size];
        }
    };

}
