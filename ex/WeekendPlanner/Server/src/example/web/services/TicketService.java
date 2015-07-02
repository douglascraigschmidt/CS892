package example.web.services;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import example.web.responses.OAuth2TokenResponse;
import example.web.responses.TicketResponse;

/**
 * The Retrofit service that interacts with the StubHub API
 */
public interface TicketService {
	
	@FormUrlEncoded
	@POST("/login")
	OAuth2TokenResponse authorize(
		@Header("Authorization") String auth,
		@Field("grant_type") String grantType,
		@Field("username") String username,
		@Field("password") String password,
		@Field("scope") String scope);
	
	@GET("/search/catalog/events/v2")
	TicketResponse queryTickets(
		@Header("Authorization") String auth,
		@Query("date") String dateRange,
		@Query("city") String city,
		@Query("maxPrice") String maxPrice,
		@Query("minTicketsAvailable") String minAvailable,
		@Query("fieldList") String fieldList,
		@Query("sort") String sort,
		@Query("limit") String limit);

}
