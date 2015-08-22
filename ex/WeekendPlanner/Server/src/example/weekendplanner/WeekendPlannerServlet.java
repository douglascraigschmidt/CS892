package example.weekendplanner;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;

import com.google.gson.Gson;

import example.web.requests.WeekendPlannerRequest;

/**
 * Servlet implementation class WeekendPlannerServlet provides a servlet
 * that responds to WeekendPlannerRequests. It uses Java 8 CompletableFutures
 * to asynchronously compose a WeekendPlannerResponse using minimal state
 * outside of what is required for the response. WeekendPlannerServlet uses
 * WeekendPlannerOps as an interface for making the various API calls
 * asynchronously and concurrently. 
 */
@WebServlet(asyncSupported = true,
			urlPatterns = { "/WeekendPlannerServlet" })
public class WeekendPlannerServlet extends HttpServlet {
	
	/**
	 * Default serialization constant
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Google's JSON parsing library
	 */
	private final Gson mGson;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeekendPlannerServlet() {
        super();
        mGson = new Gson();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
						  HttpServletResponse response)
								  throws ServletException, IOException {
		String country = request.getParameter("country");
		
		WeekendPlannerOps wOps = new WeekendPlannerOps();
		
		// Initialize the monad by retrieving the flight authorization token
		wOps.getFlightAuthToken()
		// Then get the available cities for flights from the Sabre cities API
		.thenCompose(authToken -> wOps.getCities(country, authToken))
		// Finally send the list of available cities to the client or
		// an exception object if either the authorization or city query failed
		.handle((success, exception) -> 
			completionOrError(response, success, exception))
		.join();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
								  throws ServletException, IOException {
		WeekendPlannerRequest req =
			mGson.fromJson(request.getReader().readLine(),
						   WeekendPlannerRequest.class);
		
		System.out.println(req.toString());
		
		// Make a new Operations object for each request to prevent races
		WeekendPlannerOps wOps = new WeekendPlannerOps();
		
		// Initialize the monad with the request data
		wOps.initTrip(req)
		// Combine the flight authorization API call and the current
		// state of the trip by retrieving the available flight data 
		// from the Sabre flights API (getFlight)
		.thenCombine(
			wOps.getFlightAuthToken(),
			wOps::getFlight)
		// Then do the same for ticket operations from the StubHub API
		.thenCombine(
			wOps.getTicketAuthToken(),
			wOps::getTickets)
		// Then retrieve the weather for the weekend from the 
		// OpenWeatherMap API
		.thenCompose(wOps::getWeather)
		// combine the weather information with the geocode (lat, lng) of the
		// destination city to fill the weekend with fun places that
		// don't require tickets from the Google Places API
		.thenCombine(
			wOps.getGeocode(req.getDestinationCity().getName()),
			wOps::fillWeekend)
		// Finally, send the WeekendPlannerResponse to the client,
		// or the exception object if the computation failed at any point
		.handle((success, exception) ->
			completionOrError(response, success, exception))
		.join();
	}
	
	/**
	 * Sends either the response or the error back to the client
	 */
	private <T> Boolean completionOrError(HttpServletResponse response,
			T responseObj, Throwable exception) {
		return exception == null ?
			sendResponse(response, responseObj, Response.SC_OK) :
			sendResponse(response, exception.getMessage(),
				Response.SC_BAD_REQUEST);
	}
	
	/**
	 * Generic method for sending a response object JSON to the client
	 */
	private <T> Boolean sendResponse(HttpServletResponse response,
			T responseObj, Integer status) {
		String responseJson = mGson.toJson(responseObj);
		System.out.println("Sending response to client: " + responseJson);
		response.setStatus(status);
		response.setContentType("application/json");
		response.setContentLength(responseJson.getBytes().length);
		
		try (PrintWriter pw = response.getWriter()) {
			pw.write(responseJson);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
