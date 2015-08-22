package example.web.services;

import example.web.requests.WeekendPlannerRequest;
import example.web.responses.CityResponse;
import example.web.responses.WeekendPlannerResponse;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface WeekendPlannerService {
	
	@GET("/WeekendPlannerServlet")
	CityResponse queryCities(@Query("country") String country);

	@POST("/WeekendPlannerServlet")
	WeekendPlannerResponse weekendize(@Body WeekendPlannerRequest request);
	
}
