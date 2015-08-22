package example.web.responses;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import example.web.model.City;

public class CityResponse implements Parcelable {

	/**
	 * The list of available cities from the Sabre flights API
	 */
	@SerializedName("Cities")
	private List<City> mCities;
	
	/**
	 * The error message from the server to display to the
	 * user via a Toast in the case of an exception
	 */
	private String mError;
	
	public CityResponse() {
		mCities = null;
		mError = null;
	}
	
	/**
	 * Teach the CityResponse how to create
	 * itself from a parcel created in writeToParcel()
	 */
	@SuppressWarnings("unchecked")
	public CityResponse(Parcel source) {
		mCities = (List<City>) source.readValue(null);
	}
	
	public List<City> getCities() {
		return mCities;
	}
	
	public CityResponse setError(String errorMessage) {
		mError = errorMessage;
		return this;
	}
	
	public String getError() {
		return mError;
	}
	
	/**
	 * Required by the Parcelable interface within the Android
	 * framework
	 */
	@Override
	public int describeContents() {
		// No special contents (i.e. FileDescriptors)
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mCities);
	}
	
	public static final Parcelable.Creator<CityResponse>
			CREATOR = new Creator<CityResponse>() {
	    @Override
	    public CityResponse createFromParcel(Parcel source) {
	    	return new CityResponse(source);
	    }

	    @Override
	    public CityResponse[] newArray(int size) {
	        return new CityResponse[size];
	    }
	};
	
}
