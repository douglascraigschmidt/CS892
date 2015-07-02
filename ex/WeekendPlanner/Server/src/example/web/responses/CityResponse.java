package example.web.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import example.web.model.City;

/**
 * The top-level POJO returned by the Sabre Cities API.
 * This class needs no accessor methods because the response is
 * passed directly on to the client which does the processing
 */
public class CityResponse {

	/**
	 * The valid cities for which flights may exist between
	 */
	@SerializedName("Cities")
	private List<City> mCities;
	
}
