package example.web.ops;

import retrofit.RetrofitError;
import example.web.responses.OAuth2TokenResponse;
import example.web.responses.TicketResponse;
import example.web.services.TicketService;
import example.web.utils.BaseOAuth2Utils;
import example.web.utils.TicketAuthUtils;

/**
 * Provides API-specific constants and an interface to interact
 * with the StubHub API
 */
public class TicketOps extends BaseOps<TicketService> {
	
	/**
	 * Default values
	 */
	private final String MIN_AVAILABLE = "1";
	private final String FIELD_LIST =
		"title, dateLocal, ticketInfo, venue, categories, groupings";
	private final String SORT = "dateLocal asc, minPrice desc";
	private final String LIMIT = "500";
	
	public TicketOps(String endpoint) {
		super(endpoint, TicketService.class, new TicketAuthUtils());
	}

	/**
	 * Authorizes the StubHub query, which requires only the provided
	 * app token, and therefore does not need an additional authorize query
	 * like what is required for the Sabre API calls
	 */
	@Override
	protected OAuth2TokenResponse authorize() {
		logExecutionTime("TicketOps::authorize");
		return new OAuth2TokenResponse(
			mAuthUtils.makeCredential(BaseOAuth2Utils.APP_TOKEN));
	}
	
	/**
	 * Queries the StubHub API for tickets to events in the given
	 * destination city in the given time range, at no higher than
	 * the maximum price
	 */
	public TicketResponse getTickets(String authToken,
			String dateTimeRange, String city, String maxPrice) {
		logExecutionTime("TicketOps::getTickets");
		try {
			return mService.queryTickets(
				authToken,
				dateTimeRange,
				city,
				maxPrice,
				MIN_AVAILABLE,
				FIELD_LIST,
				SORT,
				LIMIT);
		} catch (RetrofitError e) {
			// Catch and further detail the Retrofit error
			throw new RuntimeException(
				"Error getting tickets: "
				+ "There are likely no event postings this weekend in "
				+ city);
		}
	}

}
