package example.web.model;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO representing the relevant fields of a geocode of a city
 * from the Google Geocode API
 */
public class Geocode {
	
	@SerializedName("geometry")
	private Geometry mLocationCoords;
	
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
	 * A nested POJO upon which the Geocode POJO relies
	 */
	public class Geometry {
		
		@SerializedName("location")
		private LatLng mLocation;
		
		public String getLat() {
			return mLocation.getLat();
		}
		
		public String getLng() {
			return mLocation.getLng();
		}
	}
	
	/**
	 * A nested POJO upon which the Geocode POJO relies
	 */
	public class LatLng {
		
		@SerializedName("lat")
		private String mLat;
		
		@SerializedName("lng")
		private String mLng;
		
		public String getLat() {
			return mLat;
		}
		
		public String getLng() {
			return mLng;
		}
	}

}
