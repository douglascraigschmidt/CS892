package example.web.responses;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import example.web.model.City;
import example.web.model.Event;
import example.web.model.Flight;
import example.web.model.TripVariant;
import example.web.model.Weather;

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
public class WeekendPlannerResponse implements Parcelable {
	
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
	private Double mInitBudget;
	
	/**
	 * The origin city provided by the client
	 */
	@SerializedName("originCity")
	private City mOriginCity;
	
	/**
	 * The destination city provided by the client
	 */
	@SerializedName("destinationCity")
	private City mDestinationCity;
	
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
	 * The error message from the server to display to the
	 * user via a Toast in the case of an exception
	 */
	private String mError;
	
	public WeekendPlannerResponse() {
		mTripVariants = null;
		mInitBudget = null;
		mOriginCity = null;
		mDestinationCity = null;
		mFlight = null;
		mWeather = null;
		mError = null;
	}
	
	/**
	 * Teach the WeekendPlannerResponse how to create
	 * itself from a parcel created in writeToParcel()
	 */
	@SuppressWarnings("unchecked")
	public WeekendPlannerResponse(Parcel source) {
		mTripVariants = (List<TripVariant>) source.readValue(null);
		mInitBudget = source.readDouble();
		mOriginCity = (City) source.readValue(null);
		mDestinationCity = (City) source.readValue(null);
		mFlight = (Flight) source.readValue(null);
		mWeather = (List<Weather>) source.readValue(null);
	}
	
	public List<TripVariant> getTripVariants() {
		return mTripVariants;
	}
	
	public Double getInitialBudget() {
		return mInitBudget;
	}
	
	public String getOriginCityCode() {
		return mOriginCity.getCode();
	}
	
	public String getOriginCityName() {
		return mOriginCity.getName();
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
	
	public String getDepartingDepartureDateTime() {
		return mFlight.getDepartingDepartureDateTime();
	}
	
	public String getDepartingArrivalDateTime() {
		return mFlight.getDepartingArrivalDateTime();
	}
	
	public String getReturningDepartureDateTime() {
		return mFlight.getReturningDepartureDateTime();
	}
	
	public List<Weather> getWeather() {
		return mWeather;
	}
	
	public WeekendPlannerResponse setError(String errorMessage) {
		mError = errorMessage;
		return this;
	}
	
	public String getError() {
		return mError;
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

	/**
	 * Required by the Parcelable interface within the Android
	 * framework
	 */
	@Override
	public int describeContents() {
		// No special contents (i.e. FileDescriptors)
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mTripVariants);
		dest.writeDouble(mInitBudget);
		dest.writeValue(mOriginCity);
		dest.writeValue(mDestinationCity);
		dest.writeValue(mFlight);
		dest.writeValue(mWeather);
	}
	
	public static final Parcelable.Creator<WeekendPlannerResponse>
			CREATOR = new Creator<WeekendPlannerResponse>() {
	    @Override
	    public WeekendPlannerResponse createFromParcel(Parcel source) {
	    	return new WeekendPlannerResponse(source);
	    }

	    @Override
	    public WeekendPlannerResponse[] newArray(int size) {
	        return new WeekendPlannerResponse[size];
	    }
	};

}
