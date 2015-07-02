package example.web.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import example.web.model.Weather;

/**
 * The top-level POJO returned by the OpenWeatherMap API
 */
public class WeatherResponse {
	
	/**
	 * Default values
	 */
	private static final Integer WEEKEND_LENGTH = 3;

	/**
	 * List representing the forecasts of each day
	 */
	@SerializedName("list")
	private List<Weather> mWeather;
	
	/**
	 * Returns only the final 3 or less days of the response, which
	 * will always represent the days relevant to the weekend
	 */
	public List<Weather> getWeekendWeather() {
		return mWeather.size() > WEEKEND_LENGTH ?
			mWeather.subList(
				mWeather.size() - WEEKEND_LENGTH, mWeather.size()) :
			mWeather;
	}
	
	public String toString() {
		return "Weekend Forecast: " + getWeekendWeather();
	}
	
}
