package example.web.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import example.web.model.Event;

/**
 * The top-level POJO returned by the StubHub API
 */
public class TicketResponse {
	
	/**
	 * The events returned by the query
	 */
	@SerializedName("events")
	private List<Event> mEvents;
	
	public List<Event> getEvents() {
		return mEvents;
	}

}
