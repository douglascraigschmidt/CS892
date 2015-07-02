package example.web.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO that includes the relevant fields of the
 * Weather object from the OpenWeatherMap API necessary
 * to the WeekendResponse
 */
public class Weather {
	
	@SerializedName("temp")
	private Temperature mTemperature;
	
	@SerializedName("weather")
	private List<Condition> mConditions;
	
	public Double getDayTemperature() {
		return mTemperature.getDayTemperature();
	}
	
	public String getDayCondition() {
		return mConditions.get(0).getCondition();
	}
	
	@Override
	public String toString() {
		return "Temperature: " + mTemperature + "F, Conditions:" + mConditions;
	}

	/**
	 * A nested POJO upon which the Weather object relies
	 */
	public class Temperature {
		
		@SerializedName("day")
		private Double mDayTemperature;
		
		public Double getDayTemperature() {
			return mDayTemperature;
		}
		
		public String toString() {
			return mDayTemperature.toString();
		}
	}
	
	/**
	 * A nested POJO upon which the Weather object relies
	 */
	public class Condition {
		
		@SerializedName("main")
		private String mCondition;
		
		public String getCondition() {
			return mCondition;
		}
		
		public String toString() {
			return mCondition;
		}
	}
}
