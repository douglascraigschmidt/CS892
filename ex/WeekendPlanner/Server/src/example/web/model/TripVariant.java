package example.web.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO used in the WeekendPlannerResponse to represent
 * different variations of things to do, selectable by the user
 * based on personal preferences
 */
public class TripVariant {
	
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
	
	public TripVariant(Double currentBudget) {
		mCurrentBudget = currentBudget;
		mSchedule = new ArrayList<Event>();
		mPlaces = new ArrayList<List<Place>>();
	}
	
	public List<Event> getSchedule() {
		return mSchedule;
	}
	
	/**
	 * Add an event to the Schedule
	 */
	public Boolean addEvent(Event event) {
		if (tooExpensive(event) 
				|| !startsAfterPreviousEvent(event)
				|| alreadyGoing(event))
			return false;
		
		if (mSchedule.add(event)) {
			subtractFromBudget(event.getTicketPrice());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determine if an event is within the appropriate
	 * price range
	 */
	private Boolean tooExpensive(Event e) {
		return e.getTicketPrice().compareTo(mCurrentBudget) > 0;
	}
	
	/**
	 * Determine if this event begins after the previously scheduled event
	 */
	private Boolean startsAfterPreviousEvent(Event e) {
		return mSchedule.size() == 0 ?
			true : e.getStartDateTime().isAfter(
				mSchedule.get(mSchedule.size() - 1).getEndDateTime());
	}
	
	/**
	 * Determine if we are already going to a similar event earlier in
	 * the weekend
	 */
	private Boolean alreadyGoing(Event e) {
		return mSchedule.stream()
			.anyMatch(schedEvent ->
				schedEvent.getTitle().equalsIgnoreCase(e.getTitle()));
	}
	
	/**
	 * Add places to the list of places for that day
	 */
	public Boolean addPlaces(List<Place> places) {
		return mPlaces.add(places);
	}
	
	public Double getRemainingBudget() {
		return mCurrentBudget;
	}
	
	public Double subtractFromBudget(Double amount) {
		return mCurrentBudget -= amount;
	}
	
	public String toString() {
		return "Remaining Budget: " + mCurrentBudget + ", "
				+ mSchedule.toString() + "}";
	}
	
}
