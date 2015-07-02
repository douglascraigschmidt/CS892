package example.web.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import example.web.model.Geocode.Geometry;

/**
 * A POJO representing a place returned from the Google Places API
 */
public class Place {
	
	@SerializedName("geometry")
	private Geometry mLocationCoords;
	
	@SerializedName("name")
	private String mName;

	@SerializedName("types")
	private List<String> mTypes;
	
	public String getName() {
		return mName;
	}
	
	public String getType() {
		return mTypes.get(0);
	}
	
}
