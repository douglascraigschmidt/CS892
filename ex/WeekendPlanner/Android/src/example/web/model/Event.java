package example.web.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO representing the fields of a StubHub event necessary for
 * weekend planning
 */
public class Event implements Parcelable {
	
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
	
	@SuppressWarnings("unchecked")
	public Event(Parcel source) {
		mTitle = source.readString();
		mStartDateTime = source.readString();
		mVenue = (Venue) source.readValue(null);
		mTicketInfo = (TicketInfo) source.readValue(null);
		mCategories = (List<Category>) source.readValue(null);
		mGroupings = (List<Category>) source.readValue(null);
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getStartDateTime() {
		return mStartDateTime;
	}
	
	public String getStartTimeAsFormat(SimpleDateFormat outFormat) {
		SimpleDateFormat inFormat = 
			new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US);
		try {
			return outFormat.format(inFormat.parse(mStartDateTime));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Double getTicketPrice() {
		return mTicketInfo.getPrice();
	}
	
	public Set<String> getCategories() {
		Set<String> categories = new HashSet<String>();
		for(Category c : mCategories) {
			categories.add(c.toString());
		}
		return categories;
	}
	
	@Override
	public String toString() {
		return mTitle 
			+ " (" + mTicketInfo + ")"
			+ " " + getStartTimeAsFormat(
				new SimpleDateFormat("hh:mm a", Locale.US))
			+ " @ " + mVenue;
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
		dest.writeString(mTitle);
		dest.writeString(mStartDateTime);
		dest.writeValue(mVenue);
		dest.writeValue(mTicketInfo);
		dest.writeValue(mCategories);
		dest.writeValue(mGroupings);
	}
	
	public static final Parcelable.Creator<Event>
			CREATOR = new Creator<Event>() {
		@Override
		public Event createFromParcel(Parcel source) {
			return new Event(source);
		}
		
		@Override
		public Event[] newArray(int size) {
		    return new Event[size];
		}
	};

	/**
	 * Nested POJO upon which the Event POJO relies
	 */
	public class Venue implements Parcelable {
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
		
		public Venue(Parcel source) {
			mName = source.readString();
			mTimezone = source.readString();
			mStreetAddress = source.readString();
			mCity = source.readString();
			mState = source.readString();
		}
		
		@Override
		public String toString() {
			return mName + ", " 
					+ mStreetAddress;
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
			dest.writeString(mName);
			dest.writeString(mTimezone);
			dest.writeString(mStreetAddress);
			dest.writeString(mCity);
			dest.writeString(mState);
		}
		
		public final Parcelable.Creator<Venue>
				VENUE_CREATOR = new Creator<Venue>() {
			@Override
			public Venue createFromParcel(Parcel source) {
				return new Venue(source);
			}
			
			@Override
			public Venue[] newArray(int size) {
			    return new Venue[size];
			}
		};
	}
	
	/**
	 * Nested POJO upon which Event relies
	 */
	public class TicketInfo implements Parcelable {
		@SerializedName("minPrice")
		private String mMinPrice;

		@SerializedName("currencyCode")
		private String mCurrencyCode;
		
		public TicketInfo(Double price, String code) {
			mMinPrice = String.valueOf(price);
			mCurrencyCode = code;
		}
		
		public TicketInfo(Parcel source) {
			mMinPrice = source.readString();
			mCurrencyCode = source.readString();
		}
		
		public Double getPrice() {
			return Double.valueOf(mMinPrice);
		}
		
		@Override
		public String toString() {
			return "$" + mMinPrice;
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
			dest.writeString(mMinPrice);
			dest.writeString(mCurrencyCode);
		}
		
		public final Parcelable.Creator<TicketInfo>
				TICKETINFO_CREATOR = new Creator<TicketInfo>() {
			@Override
			public TicketInfo createFromParcel(Parcel source) {
				return new TicketInfo(source);
			}
			
			@Override
			public TicketInfo[] newArray(int size) {
			    return new TicketInfo[size];
			}
		};
	}
	
	/**
	 * Nested POJO upon which Event relies
	 */
	public class Category implements Parcelable {
		@SerializedName("id")
		private String mCategoryId;
		
		@SerializedName("name")
		private String mName;
		
		public Category(Parcel source) {
			mCategoryId = source.readString();
			mName = source.readString();
		}
		
		public String toString() {
			return mName;
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
			dest.writeString(mCategoryId);
			dest.writeString(mName);
		}
		
		public final Parcelable.Creator<Category>
				VENUE_CREATOR = new Creator<Category>() {
			@Override
			public Category createFromParcel(Parcel source) {
				return new Category(source);
			}
			
			@Override
			public Category[] newArray(int size) {
			    return new Category[size];
			}
		};
	}

}
