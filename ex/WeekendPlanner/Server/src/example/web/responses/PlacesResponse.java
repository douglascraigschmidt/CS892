package example.web.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

import example.web.model.Place;

/**
 * The top-level POJO returned by the Google Places API
 */
public class PlacesResponse {
	
	/**
	 * Default values
	 */
	private final Integer NUM_PLACES = 5;
	
	/**
	 * The places returned by the query
	 */
	@SerializedName("results")
	private List<Place> mPlaces;
	
	public List<Place> getPlaces() {
		return mPlaces;
	}
	
	/**
	 * Selects and returns NUM_PLACES places at random in a list
	 */
	public List<Place> getRandomPlaces() {
		List<Place> ret = new ArrayList<Place>(
			new Random().ints(0, mPlaces.size())
				.mapToObj(mPlaces::get)
				.limit(NUM_PLACES)
				.collect(Collectors.toSet()));
		return ret;
	}
	
}
