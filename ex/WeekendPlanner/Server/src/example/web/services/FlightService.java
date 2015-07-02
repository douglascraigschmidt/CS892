package example.web.services;

import example.web.responses.OAuth2TokenResponse;
import example.web.responses.CityResponse;
import example.web.responses.FlightResponse;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * The Retrofit service that interacts with the Sabre Flights API
 */
public interface FlightService {
	
	@FormUrlEncoded
	@POST("/v1/auth/token")
	OAuth2TokenResponse authorize(
		@Header("Authorization") String auth,
		@Field("grant_type") String value);
	
	@GET("/v1/lists/supported/cities")
	CityResponse queryCities(
		@Header("Authorization") String token,
		@Query("country") String country);
	
	@GET("/v1/shop/flights")
	FlightResponse queryFlights(
		 @Header("Authorization") String token,
		 @Query("origin") String origin,
		 @Query("destination") String destination,
		 @Query("departuredate") String departureDate,
		 @Query("returndate") String returnDate,
		 @Query("maxfare") String maxFare,
		 @Query("outbounddeparturewindow") String departureWindow,
		 @Query("inboundarrivalwindow") String returnWindow,
		 @Query("limit") String limitResponses);
									
}
