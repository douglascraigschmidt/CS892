package example.web.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import example.web.model.Geocode.Geometry;

/**
 * A POJO representing a place returned from the Google Places API
 */
public class Place implements Parcelable {
	
	@SerializedName("geometry")
	private Geometry mLocationCoords;
	
	@SerializedName("name")
	private String mName;

	@SerializedName("types")
	private List<String> mTypes;
	
	public Place(Parcel source) {
		mLocationCoords = (Geometry) source.readValue(null);
		mName = source.readString();
		mTypes = source.createStringArrayList();
	}
	
	public String getName() {
		return mName;
	}
	
	public String getType() {
		return mTypes.get(0);
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
	/**
	 * Required by the Parcelable interface within the Android
	 * framework.
	 */
	@Override
	public int describeContents() {
		// No special contents (i.e. FileDescriptors)
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mLocationCoords);
		dest.writeString(mName);
		dest.writeStringList(mTypes);
	}
	
	public static final Parcelable.Creator<Place>
			CREATOR = new Creator<Place>() {
		@Override
		public Place createFromParcel(Parcel source) {
			return new Place(source);
		}
		
		@Override
		public Place[] newArray(int size) {
		    return new Place[size];
		}
	};
	
}
