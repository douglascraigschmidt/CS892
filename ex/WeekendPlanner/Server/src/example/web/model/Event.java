package example.web.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO representing the fields of a StubHub event necessary for
 * weekend planning
 */
public class Event {

	/**
	 * Default Values
	 */
	private static final Integer DURATION_HOURS = 2;
	
	/**
	 * Member variables tied to serialization
	 */
	@SerializedName("title")
	private String mTitle;
	
	@SerializedName("dateLocal")
	private String mStartDateTime;
	
	@SerializedName("venue")
	private Venue mVenue;
	
	@SerializedName("ticketInfo")
	private TicketInfo mTicketInfo;
	
	@SerializedName("categories")
	private List<Category> mCategories;
	
	@SerializedName("groupings")
	private List<Category> mGroupings;
	
	/**
	 * Member variables maintained by the class
	 */
	private String mEndDateTime;
	
	public Event(String title, String startDateTime,
			String endDateTime, Double fare, String currencyCode) {
		mTitle = title;
		mStartDateTime = startDateTime;
		mEndDateTime = endDateTime;
		mTicketInfo = new TicketInfo(fare, currencyCode);
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public LocalDate getDate() {
		return getDateTime(mStartDateTime).toLocalDate();
	}
	
	public LocalDateTime getStartDateTime() {
		return getDateTime(mStartDateTime);
	}
	
	public LocalDateTime getEndDateTime() {
		return mEndDateTime == null ?
			getDateTime(mStartDateTime).plusHours(DURATION_HOURS) :
			getDateTime(mEndDateTime);
	}
	
	/**
	 * Parse the given string into a dateTime, first removing
	 * the additional zone correction returned by the server
	 */
	private LocalDateTime getDateTime(String dateTime) {
		return LocalDateTime.parse(
			dateTime.substring(0, dateTime.lastIndexOf("-")));
	}
	
	public Double getTicketPrice() {
		return mTicketInfo.getPrice();
	}
	
	/**
	 * Both hashCode() and equals() are overridden to modify 
	 * how a stream of Events handles the .distinct() intermediate
	 * operation. We use this to filter multiple postings of 
	 * the same event on the same day, which would make each trip 
	 * variation less unique
	 */
	@Override
	public int hashCode() {
		return getTitle().hashCode() ^ getDate().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Event))
			return false;
		if (obj == this)
			return true;
		
		Event other = (Event) obj;
		return getTitle().equalsIgnoreCase(other.getTitle()) &&
				getDate().equals(other.getDate());
	}
	
	@Override
	public String toString() {
		return mTitle 
			+ " at " + mStartDateTime
			+ " for " + mTicketInfo
			+ " located at " + mVenue;
	}

	/**
	 * Nested POJO upon which the Event POJO relies
	 */
	public class Venue {
		@SerializedName("name")
		private String mName;
		
		@SerializedName("timezone")
		private String mTimezone;
		
		@SerializedName("address1")
		private String mStreetAddress;
		
		@SerializedName("city")
		private String mCity;
		
		@SerializedName("state")
		private String mState;
		
		@Override
		public String toString() {
			return mName + ", " 
					+ mStreetAddress + " "
					+ mCity + ", " 
					+ mState;
		}
	}
	
	/**
	 * Nested POJO upon which Event relies
	 */
	public class TicketInfo {
		@SerializedName("minPrice")
		private String mMinPrice;

		@SerializedName("currencyCode")
		private String mCurrencyCode;
		
		public TicketInfo(Double price, String code) {
			mMinPrice = String.valueOf(price);
			mCurrencyCode = code;
		}
		
		public Double getPrice() {
			return Double.valueOf(mMinPrice);
		}
		
		@Override
		public String toString() {
			return "$" + mMinPrice + " " + mCurrencyCode;
		}
	}
	
	/**
	 * Nested POJO upon which Event relies
	 */
	public class Category {
		@SerializedName("id")
		private String mCategoryId;
		
		@SerializedName("name")
		private String mName;
		
		public String toString() {
			return mName;
		}
	}

}
