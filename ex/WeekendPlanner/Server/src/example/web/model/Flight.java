package example.web.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO of the relevant fields of the Flight information returned
 * by the Sabre Flights API necessary for weekend planning
 */
public class Flight {
	
	/**
	 * The itinerary and timing information of the flight
	 */
	@SerializedName("AirItinerary")
	private AirItinerary mAirItinerary;

	/**
	 * The pricing information of the flight
	 */
	@SerializedName("AirItineraryPricingInfo")
	private AirItineraryPricingInfo mAirItineraryPricingInfo;
	
	public Flight(String departureDate, String returnDate) {
		mAirItinerary = new AirItinerary(departureDate, returnDate);
		mAirItineraryPricingInfo = new AirItineraryPricingInfo();
	}
	
	public Double getFare() {
		return mAirItineraryPricingInfo.getFare();
	}
	
	public String getCurrencyCode() {
		return mAirItineraryPricingInfo.getCurrencyCode();
	}
	
	public String getDepartingDepartureDateTime() {
		return mAirItinerary.getDepartingDepartureDateTime();
	}
	
	public String getDepartingArrivalDateTime() {
		return mAirItinerary.getDepartingArrivalDateTime();
	}
	
	public String getReturningDepartureDateTime() {
		return mAirItinerary.getReturningDepartureDateTime();
	}
	
	public String getReturningArrivalDateTime() {
		return mAirItinerary.getReturningArrivalDateTime();
	}
	
	public String toString() {
		return mAirItinerary + "\n\tcosting " + mAirItineraryPricingInfo;
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	private class AirItinerary {
		@SerializedName("OriginDestinationOptions")
		private OriginDestinationOptions mOriginDestinationOptions;
		
		public AirItinerary(String departureDate, String returnDate) {
			mOriginDestinationOptions =
				new OriginDestinationOptions(departureDate, returnDate);
		}
		
		public String getDepartingDepartureDateTime() {
			return mOriginDestinationOptions.getDepartingDepartureDateTime();
		}
		
		public String getDepartingArrivalDateTime() {
			return mOriginDestinationOptions.getDepartingArrivalDateTime();
		}
		
		public String getReturningDepartureDateTime() {
			return mOriginDestinationOptions.getReturningDepartureDateTime();
		}
		
		public String getReturningArrivalDateTime() {
			return mOriginDestinationOptions.getReturningArrivalDateTime();
		}
	
		public String toString() {
			return mOriginDestinationOptions.toString();
		}
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	private class OriginDestinationOptions {
		@SerializedName("OriginDestinationOption")
		private List<FlightSegment> mOriginDestinationOption;
		
		public OriginDestinationOptions(
			String departureDate, String returnDate) {
			mOriginDestinationOption =
				Arrays.asList(new FlightSegment(departureDate, returnDate));
		}
		
		public String getDepartingDepartureDateTime() {
			return mOriginDestinationOption.get(0)
				.getDepartingDepartureDateTime();
		}
		
		public String getDepartingArrivalDateTime() {
			return mOriginDestinationOption.get(0)
				.getDepartingArrivalDateTime();
		}
		
		public String getReturningDepartureDateTime() {
			return mOriginDestinationOption.get(
					mOriginDestinationOption.size() - 1)
				.getReturningDepartureDateTime();
		}
		
		public String getReturningArrivalDateTime() {
			return mOriginDestinationOption.get(
					mOriginDestinationOption.size() - 1)
				.getReturningArrivalDateTime();
		}
		
		public String toString() {
			String options = "";
			for(FlightSegment f : mOriginDestinationOption) {
				options += "\n" + f + ",";
			}
			return options;
		}
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	private class FlightSegment {
		@SerializedName("FlightSegment")
		private List<Segment> mFlightSegment;
		
		@SerializedName("ElapsedTime")
		private Integer mElapsedTime;
		
		public FlightSegment(String departureDate, String returnDate) {
			mFlightSegment =
				Arrays.asList(
					new Segment(departureDate),
					new Segment(returnDate));
			mElapsedTime = 0;
		}
		
		public String getDepartingDepartureDateTime() {
			return mFlightSegment.get(0).getDepartureDateTime();
		}
		
		public String getDepartingArrivalDateTime() {
			return mFlightSegment.get(0).getArrivalDateTime();
		}
		
		public String getReturningDepartureDateTime() {
			return mFlightSegment.get(
				mFlightSegment.size() - 1).getDepartureDateTime();
		}
		
		public String getReturningArrivalDateTime() {
			return mFlightSegment.get(
				mFlightSegment.size() - 1).getArrivalDateTime();
		}
		
		public String toString() {
			String segments = "";
			for(Segment s : mFlightSegment) {
				segments += s.toString() + ", ";
			}
			return "\t[" + segments + "] taking " + mElapsedTime + " minutes";
		}
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	private class Segment {
		@SerializedName("DepartureAirport")
		private Airport mDepartureAirport;
		
		@SerializedName("ArrivalAirport")
		private Airport mArrivalAirport;
		
		@SerializedName("ElapsedTime")
		private Integer mElapsedTime;
		
		@SerializedName("DepartureDateTime")
		private String mDepartureDateTime;
		
		@SerializedName("ArrivalDateTime")
		private String mArrivalDateTime;
		
		@SerializedName("FlightNumber")
		private Integer mFlightNumber;
	
		@SerializedName("OperatingAirline")
		private Airline mOperatingAirline;
		
		public Segment(String departureDate) {
			mElapsedTime = 0;
			mDepartureDateTime =
				LocalDate.parse(departureDate).atTime(12, 00)
					.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			mArrivalDateTime =
				LocalDate.parse(departureDate).atTime(12, 00)
					.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		
		public String getArrivalDateTime() {
			return mArrivalDateTime;
		}
		
		public String getDepartureDateTime() {
			return mDepartureDateTime;
		}
		
		public String toString() {
			return mDepartureAirport.toString()
					+ " on " + mDepartureDateTime + " to "
					+ mArrivalAirport.toString()
					+ " on " + mArrivalDateTime
					+ " (" + mElapsedTime + " minutes)"
					+ " via flight #" + mFlightNumber
					+ " with " + mOperatingAirline.toString();
		}
	}
	
	private class Airport {
		@SerializedName("LocationCode")
		private String mLocationCode;
		
		public String toString() { 
			return mLocationCode;
		}
	}
	
	private class Airline {
		@SerializedName("Code")
		private String mCode;
		
		public String toString() {
			return mCode;
		}
	}
	
	private class AirItineraryPricingInfo {
		@SerializedName("ItinTotalFare")
		private ItinTotalFare mItinTotalFare;
		
		public AirItineraryPricingInfo() {
			mItinTotalFare = new ItinTotalFare();
		}
		
		public Double getFare() {
			return mItinTotalFare.getFare();
		}
		
		public String getCurrencyCode() {
			return mItinTotalFare.getCurrencyCode();
		}
		
		public String toString() {
			return mItinTotalFare.toString();
		}
	}
	
	private class ItinTotalFare {
		@SerializedName("TotalFare")
		private Fare mTotalFare;
		
		public ItinTotalFare() {
			mTotalFare = new Fare();
		}
		
		public Double getFare() {
			return mTotalFare.getAmount();
		}
		
		public String getCurrencyCode() {
			return mTotalFare.getCurrencyCode();
		}
		
		public String toString() {
			return mTotalFare.toString();
		}
	}
	
	private class Fare {
		@SerializedName("Amount")
		private String mAmount;
		
		@SerializedName("CurrencyCode")
		private String mCurrencyCode;
		
		public Fare() {
			mAmount = "0.0";
			mCurrencyCode = "USD";
		}
		
		public Double getAmount() {
			return Double.valueOf(mAmount);
		}
		
		public String getCurrencyCode() {
			return mCurrencyCode;
		}
		
		public String toString() {
			return "$" + mAmount + " (" + mCurrencyCode + ")";
		}
	}
	
}
