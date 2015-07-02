package example.web.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO with the relevant fields of a City object from
 * the Sabre Flights API
 */
public class City implements Parcelable {
	@SerializedName("code")
	private String mCode;
	
	@SerializedName("name")
	private String mName;
	
	/**
	 * Teach the city object to construct itself from
	 * a parcel made from writeToParcel
	 */
	public City(Parcel source) {
		mName = source.readString();
		mCode = source.readString();
	}
	
	public String getCode() {
		return mCode;
	}
	
	public String getName() {
		return mName;
	}

	/**
	 * Required by the Parcelable interface within the Android
	 * framework. We need to implement parcelable for City because
	 * we maintain a hashmap in PromptFragment, which requires that
	 * the class implements parcelable
	 */
	@Override
	public int describeContents() {
		// No special contents (i.e. FileDescriptors)
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mName);
		dest.writeString(mCode);
	}
	
	public static final Parcelable.Creator<City>
			CREATOR = new Creator<City>() {
		@Override
		public City createFromParcel(Parcel source) {
			return new City(source);
		}
		
		@Override
		public City[] newArray(int size) {
		    return new City[size];
		}
	};
	
}
