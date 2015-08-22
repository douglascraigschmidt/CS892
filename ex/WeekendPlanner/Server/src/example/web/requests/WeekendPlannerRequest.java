package example.web.requests;

import com.google.gson.annotations.SerializedName;

import example.web.model.City;

/**
 * The POJO representing the request from the client
 */
public class WeekendPlannerRequest {
	
	/**
	 * The initial budget given by the user
	 */
	@SerializedName("budget")
	private String mBudget;
	
	/**
	 * The current city of the user
	 */
	@SerializedName("currentCity")
	private City mOriginCity;
	
	/**
	 * The desired destination of the user
	 */
	@SerializedName("destinationCity")
	private City mDestinationCity;
	
	public Double getBudget() {
		return Double.valueOf(mBudget);
	}
	
	public City getOriginCity() {
		return mOriginCity;
	}
	
	public City getDestinationCity() {
		return mDestinationCity;
	}
	
	public String toString() {
		return "{budget: " + mBudget 
				+ ", curCity: "
				+ mOriginCity.getName() + " (" + mOriginCity.getCode() + ")"
				+ ", destCity: "
				+ (mDestinationCity == null ? 
					"null" :
					mDestinationCity.getName() + " (" + mDestinationCity.getCode() + ")")
				+ "}";
	}

}
