package example.web.ops;

import retrofit.RetrofitError;
import example.web.responses.CityResponse;
import example.web.responses.FlightResponse;
import example.web.responses.OAuth2TokenResponse;
import example.web.services.FlightService;
import example.web.utils.BaseOAuth2Utils;
import example.web.utils.FlightAuthUtils;

/**
 * Provides API-specific constants and an interface to interact
 * with the Sabre Flights API
 */
public class FlightOps extends BaseOps<FlightService> {
	
	/**
	 * Default flight parameters
	 */
	private final String FLIGHT_DEPARTURE_WINDOW = "17002359";
	private final String FLIGHT_RETURN_WINDOW = "12002359";
	private final String LIMIT_RESPONSES = "1";
	
	public FlightOps(String endpoint) {
		super(endpoint, FlightService.class, new FlightAuthUtils());
	}

	/**
	 * The flights API requires a User token that must be
	 * requested from and returned by the server
	 */
	@Override
	protected OAuth2TokenResponse authorize() { 
		logExecutionTime("FlightOps::authorize");
		try {
			return mService.authorize(
				mAuthUtils.makeCredential(BaseOAuth2Utils.USER_TOKEN),
				mAuthUtils.getGrantType());
		} catch (RetrofitError e) {
			// Catch and further detail the Retrofit error
			throw new RuntimeException(
				"Server Error authorizing the flight request:"
				+ "The server is likely down, "
				+ "or the credentials are invalid");
		}
	}
	
	/**
	 * Queries the Flights API for cities with available airports
	 * NOTE: because the freely available Sabre Sandbox is being used, 
	 * this is a reduced selection of all cities
	 */
	public CityResponse getCities(String authToken, String country) {
		logExecutionTime("FlightOps::getCities");
		try {
			return mService.queryCities(
				authToken,
				country);
		} catch (RetrofitError e) {
			// Catch and further detail the Retrofit error
			throw new RuntimeException(
				"Error getting cities: The server is likely down");
		}
	}
	
	/**
	 * Determines if a flight is needed, and queries the API if necessary.
	 * Otherwise it is treated as a "fun things to do in my current city"
	 * request, and the flight is mocked with a cost of $0
	 */
	public FlightResponse getFlight(
			String authToken, String origin, String destination,
			String departureDate, String returnDate, String maxFare) {
		// If the origin and destination city are the same,
		// return a mock FlightInfoResponse with a fare of $0.00
		logExecutionTime("FlightOps::getFlight");
		try {
			return origin.equals(destination) ?
				new FlightResponse(
					origin, destination, departureDate, returnDate) :
				mService.queryFlights(
					authToken,
					origin,
					destination,
					departureDate,
					returnDate,
					maxFare,
					FLIGHT_DEPARTURE_WINDOW,
					FLIGHT_RETURN_WINDOW,
					LIMIT_RESPONSES);
		} catch (RetrofitError e) {
			// Catch and further detail the Retrofit error
			throw new RuntimeException(
				"Error getting flights: "
				+ "There are likely no available flights for those cities");
		}
	}

}
