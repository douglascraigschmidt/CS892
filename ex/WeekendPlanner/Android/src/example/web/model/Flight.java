package example.web.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO of the relevant fields of the Flight information returned
 * by the Sabre Flights API necessary for weekend planning
 */
public class Flight implements Parcelable {
	
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
	
	public Flight(Parcel source) {
		mAirItinerary = (AirItinerary) source.readValue(null);
		mAirItineraryPricingInfo = 
			(AirItineraryPricingInfo) source.readValue(null);
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
	
	public String getDepartingDepartureInfo() {
		return mAirItinerary.getDepartingDepartureInfo();
	}
	
	public String getReturningDepartureDateTime() {
		return mAirItinerary.getReturningDepartureDateTime();
	}
	
	public String getReturningArrivalDateTime() {
		return mAirItinerary.getReturningArrivalDateTime();
	}
	
	public String getReturningDepartureInfo() {
		return mAirItinerary.getReturningDepartureInfo();
	}
	
	public String toString() {
		return mAirItinerary + "\n\tcosting " + mAirItineraryPricingInfo;
	}
	
	/**
	 * Required by the Parcelable interface within the Android
	 * framework.
	 */
	@Override
	public int describeContents() {
		// No special contents (i.e. FileDescriptors)
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mAirItinerary);
		dest.writeValue(mAirItineraryPricingInfo);
	}
	
	public static final Parcelable.Creator<Flight>
			CREATOR = new Creator<Flight>() {
		@Override
		public Flight createFromParcel(Parcel source) {
			return new Flight(source);
		}
		
		@Override
		public Flight[] newArray(int size) {
		    return new Flight[size];
		}
	};
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	public class AirItinerary implements Parcelable {
		@SerializedName("OriginDestinationOptions")
		private OriginDestinationOptions mOriginDestinationOptions;
		
		public AirItinerary(Parcel source) {
			mOriginDestinationOptions = 
				(OriginDestinationOptions) source.readValue(null);
		}
		
		public String getDepartingDepartureDateTime() {
			return mOriginDestinationOptions.getDepartingDepartureDateTime();
		}
		
		public String getDepartingArrivalDateTime() {
			return mOriginDestinationOptions.getDepartingArrivalDateTime();
		}
		
		public String getDepartingDepartureInfo() {
			return mOriginDestinationOptions.getDepartingDepartureInfo();
		}
		
		public String getReturningDepartureDateTime() {
			return mOriginDestinationOptions.getReturningDepartureDateTime();
		}
		
		public String getReturningArrivalDateTime() {
			return mOriginDestinationOptions.getReturningArrivalDateTime();
		}
		
		public String getReturningDepartureInfo() {
			return mOriginDestinationOptions.getReturningDepartureInfo();
		}
	
		public String toString() {
			return mOriginDestinationOptions.toString();
		}
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeValue(mOriginDestinationOptions);
		}
		
		public final Parcelable.Creator<AirItinerary>
				AIRITIN_CREATOR = new Creator<AirItinerary>() {
			@Override
			public AirItinerary createFromParcel(Parcel source) {
				return new AirItinerary(source);
			}
			
			@Override
			public AirItinerary[] newArray(int size) {
			    return new AirItinerary[size];
			}
		};
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	public class OriginDestinationOptions implements Parcelable {
		@SerializedName("OriginDestinationOption")
		private List<FlightSegment> mOriginDestinationOption;
		
		@SuppressWarnings("unchecked")
		public OriginDestinationOptions(Parcel source) {
			mOriginDestinationOption =
				(List<FlightSegment>) source.readValue(null);
		}
		
		public String getDepartingDepartureDateTime() {
			return mOriginDestinationOption.get(0)
				.getDepartingDepartureDateTime();
		}
		
		public String getDepartingArrivalDateTime() {
			return mOriginDestinationOption.get(0)
				.getDepartingArrivalDateTime();
		}
		
		public String getDepartingDepartureInfo() {
			return mOriginDestinationOption.get(0)
				.getDepartingDepartureInfo();
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
		
		public String getReturningDepartureInfo() {
			return mOriginDestinationOption.get(
					mOriginDestinationOption.size() - 1)
				.getDepartingDepartureInfo();
		}
		
		public String toString() {
			String options = "";
			for(FlightSegment f : mOriginDestinationOption) {
				options += "\n" + f + ",";
			}
			return options;
		}
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeValue(mOriginDestinationOption);
		}
		
		public final Parcelable.Creator<OriginDestinationOptions>
				ORIGDESTOPTS_CREATOR = new Creator<OriginDestinationOptions>() {
			@Override
			public OriginDestinationOptions createFromParcel(Parcel source) {
				return new OriginDestinationOptions(source);
			}
			
			@Override
			public OriginDestinationOptions[] newArray(int size) {
			    return new OriginDestinationOptions[size];
			}
		};
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	public class FlightSegment implements Parcelable {
		@SerializedName("FlightSegment")
		private List<Segment> mFlightSegment;
		
		@SerializedName("ElapsedTime")
		private Integer mElapsedTime;
		
		@SuppressWarnings("unchecked")
		public FlightSegment(Parcel source) {
			mFlightSegment = (List<Segment>) source.readValue(null);
			mElapsedTime = source.readInt();
		}
		
 		public String getDepartingDepartureDateTime() {
			return mFlightSegment.get(0).getDepartureDateTime();
		}
		
		public String getDepartingArrivalDateTime() {
			return mFlightSegment.get(0).getArrivalDateTime();
		}
		
		public String getDepartingDepartureInfo() {
			return mFlightSegment.get(0).getInfo();
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
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeValue(mFlightSegment);
			dest.writeInt(mElapsedTime);
		}
		
		public final Parcelable.Creator<FlightSegment>
				FLIGHTSEG_CREATOR = new Creator<FlightSegment>() {
			@Override
			public FlightSegment createFromParcel(Parcel source) {
				return new FlightSegment(source);
			}
			
			@Override
			public FlightSegment[] newArray(int size) {
			    return new FlightSegment[size];
			}
		};
	}
	
	/**
	 * A nested POJO upon which the Flight POJO relies
	 */
	public class Segment implements Parcelable {
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
		
		public Segment(Parcel source) {
			mDepartureAirport = (Airport) source.readValue(null);
			mArrivalAirport = (Airport) source.readValue(null);
			mElapsedTime = source.readInt();
			mDepartureDateTime = source.readString();
			mArrivalDateTime = source.readString();
			mFlightNumber = source.readInt();
			mOperatingAirline = (Airline) source.readValue(null);
		}
		
		public String getArrivalDateTime() {
			return mArrivalDateTime;
		}
		
		public String getDepartureDateTime() {
			return mDepartureDateTime;
		}
		
		public String getInfo() {
			return mFlightNumber != null ?
				"Flight #" + mFlightNumber 
					+ " (" + mOperatingAirline + ")"
					+ " from " + mDepartureAirport :
				"N/A";
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
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeValue(mDepartureAirport);
			dest.writeValue(mArrivalAirport);
			dest.writeInt(mElapsedTime);
			dest.writeString(mDepartureDateTime);
			dest.writeString(mArrivalDateTime);
			dest.writeInt(mFlightNumber);
			dest.writeValue(mOperatingAirline);
		}
		
		public final Parcelable.Creator<Segment>
				SEG_CREATOR = new Creator<Segment>() {
			@Override
			public Segment createFromParcel(Parcel source) {
				return new Segment(source);
			}
			
			@Override
			public Segment[] newArray(int size) {
			    return new Segment[size];
			}
		};
	}
	
	public class Airport implements Parcelable {
		@SerializedName("LocationCode")
		private String mLocationCode;
		
		public Airport(Parcel source) {
			mLocationCode = source.readString();
		}
		
		public String toString() { 
			return mLocationCode;
		}
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mLocationCode);
		}
		
		public final Parcelable.Creator<Airport>
				AIRPORT_CREATOR = new Creator<Airport>() {
			@Override
			public Airport createFromParcel(Parcel source) {
				return new Airport(source);
			}
			
			@Override
			public Airport[] newArray(int size) {
			    return new Airport[size];
			}
		};
	}
	
	public class Airline implements Parcelable {
		@SerializedName("Code")
		private String mCode;
		
		public Airline(Parcel source) {
			mCode = source.readString();
		}
		
		public String toString() {
			return mCode;
		}
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mCode);
		}
		
		public final Parcelable.Creator<Airline>
				AIRLINE_CREATOR = new Creator<Airline>() {
			@Override
			public Airline createFromParcel(Parcel source) {
				return new Airline(source);
			}
			
			@Override
			public Airline[] newArray(int size) {
			    return new Airline[size];
			}
		};
	}
	
	public class AirItineraryPricingInfo implements Parcelable {
		@SerializedName("ItinTotalFare")
		private ItinTotalFare mItinTotalFare;
		
		public AirItineraryPricingInfo(Parcel source) {
			mItinTotalFare = (ItinTotalFare) source.readValue(null);
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
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeValue(mItinTotalFare);
		}
		
		public final Parcelable.Creator<AirItineraryPricingInfo>
				ITINPRICING_CREATOR = new Creator<AirItineraryPricingInfo>() {
			@Override
			public AirItineraryPricingInfo createFromParcel(Parcel source) {
				return new AirItineraryPricingInfo(source);
			}
			
			@Override
			public AirItineraryPricingInfo[] newArray(int size) {
			    return new AirItineraryPricingInfo[size];
			}
		};
	}
	
	public class ItinTotalFare implements Parcelable {
		@SerializedName("TotalFare")
		private Fare mTotalFare;
		
		public ItinTotalFare(Parcel source) {
			mTotalFare = (Fare) source.readValue(null);
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
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeValue(mTotalFare);
		}
		
		public final Parcelable.Creator<ItinTotalFare>
				ITINFARE_CREATOR = new Creator<ItinTotalFare>() {
			@Override
			public ItinTotalFare createFromParcel(Parcel source) {
				return new ItinTotalFare(source);
			}
			
			@Override
			public ItinTotalFare[] newArray(int size) {
			    return new ItinTotalFare[size];
			}
		};
	}
	
	public class Fare implements Parcelable {
		@SerializedName("Amount")
		private String mAmount;
		
		@SerializedName("CurrencyCode")
		private String mCurrencyCode;
		
		public Fare(Parcel source) {
			mAmount = source.readString();
			mCurrencyCode = source.readString();
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
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mAmount);
			dest.writeString(mCurrencyCode);
		}
		
		public final Parcelable.Creator<Fare>
				FARE_CREATOR = new Creator<Fare>() {
			@Override
			public Fare createFromParcel(Parcel source) {
				return new Fare(source);
			}
			
			@Override
			public Fare[] newArray(int size) {
			    return new Fare[size];
			}
		};
	}
	
}
