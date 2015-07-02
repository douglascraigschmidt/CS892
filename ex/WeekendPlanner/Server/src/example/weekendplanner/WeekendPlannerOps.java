package example.weekendplanner;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import example.web.model.Weather;
import example.web.ops.FlightOps;
import example.web.ops.PlacesOps;
import example.web.ops.TicketOps;
import example.web.ops.WeatherOps;
import example.web.requests.WeekendPlannerRequest;
import example.web.responses.CityResponse;
import example.web.responses.GeoCodeResponse;
import example.web.responses.PlacesResponse;
import example.web.utils.DateUtils;

/**
 * WeekendPlannerOps is a thin layer between the WeekendPlannerServlet
 * and the individual API operations classes. The point of this class
 * is to unify the interfaces of accessing asynchronous calls, enabling
 * them to be composed freely. It also maintains the executor upon which each
 * asynchronous call is run.
 */
public class WeekendPlannerOps {
	
	/**
	 * Default values
	 */
	private final int THREAD_COUNT = 8;
	private final int NUM_TRIP_VARIANTS = 5;
	private final String FLIGHT_ENDPOINT = "https://api.test.sabre.com/";
	private final String TICKET_ENDPOINT = "https://api.stubhubsandbox.com/";
	private final String WEATHER_ENDPOINT = "http://api.openweathermap.org/";
	private final String PLACES_ENDPOINT = "https://maps.googleapis.com/";
	
	/**
	 * The executor responsible for scheduling threads
	 */
	private Executor mExecutor;
	
	/**
	 * Helper classes for interacting with the various APIs
	 */
	private FlightOps mFlightOps;
	private TicketOps mTicketOps;
	private WeatherOps mWeatherOps;
	private PlacesOps mPlacesOps;
	
	public WeekendPlannerOps() {
		init(makeDefaultExecutor(THREAD_COUNT));
	}
	
	public WeekendPlannerOps(WeekendPlannerRequest req, int numThreads) {
		init(makeDefaultExecutor(numThreads));
	}
	
	public WeekendPlannerOps(WeekendPlannerRequest req, Executor executor) {
		init(executor);
	}
	
	/**
	 * Initialize the Ops with the given executor
	 */
	private void init(Executor exec) {
		mExecutor = exec;
		
		mFlightOps = new FlightOps(FLIGHT_ENDPOINT);
		mTicketOps = new TicketOps(TICKET_ENDPOINT);
		mWeatherOps = new WeatherOps(WEATHER_ENDPOINT);
		mPlacesOps = new PlacesOps(PLACES_ENDPOINT);
	}
	
	public Executor getExecutor() {
		return mExecutor;
	}
	
	/**
	 * Construct a default executor that uses Daemon threads
	 * that are not able to keep the program alive in the case
	 * of an early termination in the case of, say, a thrown exception
	 */
	private Executor makeDefaultExecutor(int numThreads) {
		return Executors.newFixedThreadPool(
			numThreads,
			new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setDaemon(true);
					return t;
				}
			});
	}
	
	/**
	 * Initialize the trip with the given request, so that
	 * state does not have to be kept redundantly
	 */
	public CompletableFuture<WeekendPlannerResponse> initTrip(
			WeekendPlannerRequest req) {
		// Use .completedFuture so that the interface is consistent
		// with the other operations in the pipeline. This call
		// is useful for the beginning of a pipeline
		return CompletableFuture.completedFuture(
			new WeekendPlannerResponse(
				req.getBudget(),
				req.getOriginCity(),
				req.getDestinationCity(),
				NUM_TRIP_VARIANTS));
	}
	
	/**
	 * Return a token that will authorize requests to the StubHub API
	 */
	public CompletableFuture<String> getFlightAuthToken() {
		return CompletableFuture.supplyAsync(
			() -> mFlightOps.getAuthToken(),
			getExecutor());
	}
	
	/**
	 * Return a token that will authorize requests to the StubHub API
	 */
	public CompletableFuture<String> getTicketAuthToken() {
		return CompletableFuture.supplyAsync(
			() -> mTicketOps.getAuthToken(),
			getExecutor());
	}
	
	/**
	 * Get the available cities for which flights may exist
	 * according to the Sabre Cities API
	 */
	public CompletableFuture<CityResponse> getCities(
			String country, String authToken) {
		return CompletableFuture.supplyAsync(
			() -> mFlightOps.getCities(authToken, country),
			getExecutor());
	}

	/**
	 * Get flight information from the Sabre Flights API for the
	 * appropriate dates between the given origin and destination
	 * @throws Exception 
	 */
	public WeekendPlannerResponse getFlight(
			WeekendPlannerResponse tripVariants, String authToken) {
		return CompletableFuture.supplyAsync(
				() -> mFlightOps.getFlight(
					authToken,
					tripVariants.getOriginCityCode(),
					tripVariants.getDestinationCityCode(),
					DateUtils.getFormattedDateOfNext(DayOfWeek.FRIDAY),
					DateUtils.getFormattedDateOfNext(DayOfWeek.SUNDAY),
					String.valueOf(tripVariants.getInitialBudget())),
				getExecutor())
			.thenApply(tripVariants::update)
			.join();
	}
	
	/**
	 * Return tickets to relevant events going on in the destiantion city
	 * on the appropriate weekend
	 */
	public WeekendPlannerResponse getTickets(
			WeekendPlannerResponse tripVariants, String authToken) {
		// These methods will return a server throttle error because requests are made
		// too quickly
		/**
		List<CompletableFuture<TicketInfoResponse>> responses =
		Arrays.asList(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
			.stream()
			.map(day -> getTicketsForDay(day, tripVariants, authToken))
			.collect(Collectors.toList());
			
		// Apply the update function for each response
		responses.stream()
			.map(CompletableFuture::join)
			.forEach(tripVariants::update);
		
		// Keep interface consistent
		return CompletableFuture.completedFuture(tripVariants);
	----------------------------------------------------------------------------
		return getTicketsForDay(DayOfWeek.FRIDAY, tripVariants, authToken)
			.thenComposeAsync(variants ->
				getTicketsForDay(DayOfWeek.SATURDAY, variants, authToken))
			.thenComposeAsync(variants ->
				getTicketsForDay(DayOfWeek.SUNDAY, variants, authToken));
		*/
		
		// As such, the request cannot be broken into days
		return CompletableFuture.supplyAsync(
				() -> mTicketOps.getTickets(
					authToken,
					DateUtils.makeDateTimeRange(tripVariants.getFlight()),
					tripVariants.getDestinationCityName(),
					String.valueOf(tripVariants.getBudgetAfterFlight())),
				getExecutor())
			.thenApply(tripVariants::update)
			.join();
	}
	
	/**
	 * Return the weather for as many days as required to capture
	 * the weekend of the trip
	 */
	public CompletableFuture<WeekendPlannerResponse> getWeather(
			WeekendPlannerResponse tripVariants) {
		return CompletableFuture.supplyAsync(
				() -> mWeatherOps.getWeather(
					tripVariants.getDestinationCityName(),
					DateUtils.getNumDaysUntilNext(DayOfWeek.MONDAY)),
				getExecutor())
			.thenApply(tripVariants::update);
	}
	
	/**
	 * Convert a city name to its lat, lng coordinates using
	 * the Google Geocoding API
	 */
	public CompletableFuture<GeoCodeResponse> getGeocode(
			String destinationCityName) {
		return CompletableFuture.supplyAsync(
			() -> mPlacesOps.getGeocode(
				mPlacesOps.getAuthToken(),
				destinationCityName),
			getExecutor());
	}
	
	/**
	 * Fill the weekend with generally free or inexpensive activities
	 * tailored to the weather on the given day
	 */
	public WeekendPlannerResponse fillWeekend(
			WeekendPlannerResponse tripVariants, GeoCodeResponse geocode) {
		// Here, the server handles quotas differently, and the server is able to
		// handle concurrent day-level requests
		List<CompletableFuture<PlacesResponse>> responses =
		tripVariants.getWeather().stream()
			.map(dayWeather -> getPlacesForDay(dayWeather, geocode))
			.collect(Collectors.toList());
		
		// When each places request returns, update the response
		responses.stream()
			.map(CompletableFuture::join)
			.filter(place -> place != null)
			.forEach(tripVariants::update);

		return tripVariants;
	}
	
	/**
	 * Return places near a given (lat, lng) appropriate for given weather
	 */
	private CompletableFuture<PlacesResponse> getPlacesForDay(
			Weather dayWeather, GeoCodeResponse geocode) {
		return CompletableFuture.supplyAsync(
			() -> mPlacesOps.getPlaces(
				mPlacesOps.getAuthToken(),
				geocode.getLat(),
				geocode.getLng(),
				dayWeather),
			getExecutor());
	}

}
