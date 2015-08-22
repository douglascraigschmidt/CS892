package example.web.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO used in the WeekendPlannerResponse to represent
 * different variations of things to do, selectable by the user
 * based on personal preferences
 */
public class TripVariant implements Parcelable {
	
	/**
	 * The current budget remaining for this variant
	 */
	@SerializedName("currentBudget")
	private Double mCurrentBudget;
	
	/**
	 * The schedule of events for this variant
	 */
	@SerializedName("schedule")
	private List<Event> mSchedule;
	
	/**
	 * A List of lists of places that represent things to do
	 * that don't necessarily require tickets. Each item in
	 * the outer list represents a day, and each inner list
	 * represents place suggestions for that day based on the weather
	 */
	@SerializedName("places")
	private List<List<Place>> mPlaces;
	
	@SuppressWarnings("unchecked")
	public TripVariant(Parcel source) {
		mCurrentBudget = source.readDouble();
		mSchedule = (List<Event>) source.readValue(null);
		mPlaces = (List<List<Place>>) source.readValue(null);
	}
	
	public List<Event> getSchedule() {
		return mSchedule;
	}
	
	public Map<String, List<Event>> getScheduleByDay() {
		Map<String, List<Event>> organizedByDay = 
			new HashMap<String, List<Event>>();
		SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.US);
		
		for(Event e : mSchedule) {
			String startTime = e.getStartTimeAsFormat(outFormat);
			if (!organizedByDay.containsKey(startTime))
				organizedByDay.put(startTime, new ArrayList<Event>());
			organizedByDay.get(startTime).add(e);
		}
		return organizedByDay;
	}
	
	public Set<String> getCategories() {
		Set<String> categories = new HashSet<String>();
		for(Event e : mSchedule) {
			categories.addAll(e.getCategories());
		}
		return categories;
	}
	
	public Set<String> getTypes() {
		Set<String> types = new HashSet<String>();
		for(List<Place> list : mPlaces) {
			for(Place p : list) {
				types.add(p.getType());
			}
		}
		return types;
	}
	
	public Double getRemainingBudget() {
		return mCurrentBudget;
	}
	
	public Double subtractFromBudget(Double amount) {
		return mCurrentBudget -= amount;
	}
	
	public List<List<Place>> getPlaces() {
		return mPlaces;
	}
	
	public String toString() {
		return "Remaining Budget: " + mCurrentBudget + ", "
				+ mSchedule.toString() + "}";
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
		dest.writeDouble(mCurrentBudget);
		dest.writeValue(mSchedule);
		dest.writeValue(mPlaces);
	}
	
	public static final Parcelable.Creator<TripVariant>
			CREATOR = new Creator<TripVariant>() {
		@Override
		public TripVariant createFromParcel(Parcel source) {
			return new TripVariant(source);
		}
		
		@Override
		public TripVariant[] newArray(int size) {
		    return new TripVariant[size];
		}
	};
	
}
