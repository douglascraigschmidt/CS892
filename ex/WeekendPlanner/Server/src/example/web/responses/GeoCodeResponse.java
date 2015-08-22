package example.web.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import example.web.model.Geocode;

/**
 * The top-level POJO returned by the Google Geocode API
 */
public class GeoCodeResponse {
	
	/**
	 * The Geocoding response
	 */
	@SerializedName("results")
	private List<Geocode> mGeocodes;
	
	public String getLat() {
		return mGeocodes.get(0).getLat();
	}
	
	public String getLng() {
		return mGeocodes.get(0).getLng();
	}

	public String toString() {
		return mGeocodes.toString();
	}
}
