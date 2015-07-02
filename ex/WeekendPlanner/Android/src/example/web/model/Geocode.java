package example.web.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO representing the relevant fields of a geocode of a city
 * from the Google Geocode API
 */
public class Geocode implements Parcelable {
	
	@SerializedName("geometry")
	private Geometry mLocationCoords;
	
	public Geocode(Parcel source) {
		mLocationCoords = (Geometry) source.readValue(null);
	}
	
	public String getLat() {
		return mLocationCoords.getLat();
	}
	
	public String getLng() {
		return mLocationCoords.getLng();
	}
	
	public String toString() {
		return "{" + getLat() + "," + getLng() + "}";
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
	}
	
	public static final Parcelable.Creator<Geocode>
			CREATOR = new Creator<Geocode>() {
		@Override
		public Geocode createFromParcel(Parcel source) {
			return new Geocode(source);
		}
		
		@Override
		public Geocode[] newArray(int size) {
		    return new Geocode[size];
		}
	};
	
	/**
	 * A nested POJO upon which the Geocode POJO relies
	 */
	public class Geometry implements Parcelable {
		@SerializedName("location")
		private LatLng mLocation;
		
		public Geometry(Parcel source) {
			mLocation = (LatLng) source.readValue(null);
		}
		
		public String getLat() {
			return mLocation.getLat();
		}
		
		public String getLng() {
			return mLocation.getLng();
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
			dest.writeValue(mLocation);
		}
		
		public final Parcelable.Creator<Geometry>
				GEOMETRY_CREATOR = new Creator<Geometry>() {
			@Override
			public Geometry createFromParcel(Parcel source) {
				return new Geometry(source);
			}
			
			@Override
			public Geometry[] newArray(int size) {
			    return new Geometry[size];
			}
		};
	}
	
	/**
	 * A nested POJO upon which the Geocode POJO relies
	 */
	public class LatLng implements Parcelable {
		@SerializedName("lat")
		private String mLat;
		
		@SerializedName("lng")
		private String mLng;
		
		public LatLng(Parcel source) {
			mLat = source.readString();
			mLng = source.readString();
		}
		
		public String getLat() {
			return mLat;
		}
		
		public String getLng() {
			return mLng;
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
			dest.writeString(mLat);
			dest.writeString(mLng);
		}
		
		public final Parcelable.Creator<LatLng>
				LATLNG_CREATOR = new Creator<LatLng>() {
			@Override
			public LatLng createFromParcel(Parcel source) {
				return new LatLng(source);
			}
			
			@Override
			public LatLng[] newArray(int size) {
			    return new LatLng[size];
			}
		};
	}

}
