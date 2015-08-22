package example.weekendplanner;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import example.web.model.City;
import example.web.model.Event;
import example.web.model.Flight;
import example.web.model.TripVariant;
import example.web.model.Weather;
import example.web.responses.FlightResponse;
import example.web.responses.PlacesResponse;
import example.web.responses.TicketResponse;
import example.web.responses.WeatherResponse;

/**
 * WeekendPlannerResponse is responsible for holding the
 * response weekend data sent back to the client. The
 * asynchronous operations directly update this response
 * so that additional state beyond what is needed for the
 * weekend response is not held. It is also to centralize
 * computations relating to planning the weekend, such as
 * selecting the various events for each day, tracking
 * remaining budget for each variant of the weekend, etc.
 */
public class WeekendPlannerResponse {
	
	/**
	 * A list of different variations of things to do
	 * at the destination city this weekend, each keeping
	 * track of the individual remaining budget
	 */
	@SerializedName("tripVariants")
	private List<TripVariant> mTripVariants;
	
	/**
	 * The initial budget provided by the client
	 */
	@SerializedName("initBudget")
	private final Double mInitBudget;
	
	/**
	 * The origin city provided by the client
	 */
	@SerializedName("originCity")
	private final City mOriginCity;
	
	/**
	 * The destination city provided by the client
	 */
	@SerializedName("destinationCity")
	private final City mDestinationCity;
	
	/**
	 * The flight used for the trip, returned by
	 * WeekendPlannerOps::getFlight which queries the Sabre Flights API
	 */
	@SerializedName("flight")
	private Flight mFlight;
	
	/**
	 * The weekend weather forecast at the destination city
	 */
	@SerializedName("weather")
	private List<Weather> mWeather;
	
	/**
	 * An iterator used in distributing events across the trip variants
	 * Note: used internally, does not need to be serialized
	 */
	@Expose(serialize = false, deserialize = false)
	private Iterator<TripVariant> mEventIt;

	/**
	 * Initialize the Response with the given parameters
	 */
	public WeekendPlannerResponse(Double initBudget,
			City originCity, City destCity, int numVariants) {
		mInitBudget = initBudget;
		mOriginCity = originCity;
		mDestinationCity = destCity;
		mTripVariants = 
			Stream.generate(() -> new TripVariant(mInitBudget))
				.limit(numVariants)
				.collect(Collectors.toList());
	}
	
	public Double getInitialBudget() {
		return mInitBudget;
	}
	
	public String getOriginCityCode() {
		return mOriginCity.getCode();
	}
	
	public String getDestinationCityCode() {
		return mDestinationCity.getCode();
	}
	
	public String getDestinationCityName() {
		return mDestinationCity.getName();
	}
	
	public Double getBudgetAfterFlight() {
		return mInitBudget - mFlight.getFare();
	}
	
	public Flight getFlight() {
		return mFlight;
	}
	
	public LocalDateTime getDepartingArrivalDateTime() {
		return LocalDateTime.parse(mFlight.getDepartingArrivalDateTime());
	}
	
	public LocalDateTime getReturningDepartureDateTime() {
		return LocalDateTime.parse(mFlight.getReturningDepartureDateTime());
	}
	
	public List<Weather> getWeather() {
		return mWeather;
	}
	
	/**
	 * Set the flight returned from the Sabre API 
	 * and return the updated response
	 */
	public WeekendPlannerResponse update(FlightResponse response) {
		mFlight = response.getFlight();
		mTripVariants.forEach(trip ->
			trip.subtractFromBudget(response.getFare()));
		return this;
	}
	
	/**
	 * Distribute the events returned from the StubHub API across
	 * the trip variants and return the updated response
	 */
	public WeekendPlannerResponse update(TicketResponse response) {
		mEventIt = mTripVariants.iterator();
		if (response.getEvents() != null) {
			response.getEvents().stream()
				.distinct()
				.forEach(event -> {
					if (mEventIt.hasNext()) 
						mEventIt.next().addEvent(event);
					else
						mEventIt = mTripVariants.iterator();
				});
		}
		return this;
	}
	
	/**
	 * Set the weekend weather returned from the OpenWeatherMap API
	 * and return the updated response
	 */
	public WeekendPlannerResponse update(WeatherResponse response) {
		mWeather = response.getWeekendWeather();
		return this;
	}
	
	/**
	 * Add various places returned from the Google Places API
	 * to each trip variant and return the updated response
	 */
	public WeekendPlannerResponse update(PlacesResponse response) {
		mTripVariants
			.forEach(trip -> trip.addPlaces(response.getRandomPlaces()));
		return this;
	}
	
	public String toString() {
		String variants = "";
		for(TripVariant tv : mTripVariants) {
			String events = "";
			for(Event e : tv.getSchedule()) {
				events += "\n\t\t" + e;
			}
			variants += "\n\tVariant:" + events;
		}
		return "Flight: " + mFlight + "\nVariants:" + variants;
	}

}
