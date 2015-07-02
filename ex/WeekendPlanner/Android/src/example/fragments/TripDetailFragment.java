package example.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import example.web.model.Event;
import example.web.model.Place;
import example.web.model.TripVariant;
import example.web.model.Weather;
import example.weekendizer.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TripDetailFragment extends Fragment {
	
	/**
	 * A tag by which to find the Fragment in the stack
	 */
	public static final String FRAGMENT_TAG = "tripdetail_fragment";
	
	/**
	 * Relevant UI widgets
	 */
	private TextView mVariantTitle;
	private TextView mBudgetAmount;
	private ListView mDays;
	
	/**
	 * The names for the arguments relevant to this fragment
	 */
	public static final String POSITION_ARG = "position";
	public static final String VARIANT_ARG = "variant";
	public static final String FRIDAY_WEATHER_ARG = "friday";
	private static final Integer FRIDAY_INDEX = 0;
	public static final String SATURDAY_WEATHER_ARG = "saturday";
	private static final Integer SATURDAY_INDEX = 1;
	public static final String SUNDAY_WEATHER_ARG = "sunday";
	private static final Integer SUNDAY_INDEX = 2;

	public static TripDetailFragment newInstance(Integer variantNumber,
			TripVariant variant, List<Weather> weather) {
		TripDetailFragment tripDetailFragment = new TripDetailFragment();
		
		Bundle arguments = new Bundle();
		arguments.putInt(POSITION_ARG, variantNumber);
		arguments.putParcelable(VARIANT_ARG, variant);
		arguments.putParcelable(
			FRIDAY_WEATHER_ARG, weather.get(FRIDAY_INDEX));
		arguments.putParcelable(
			SATURDAY_WEATHER_ARG, weather.get(SATURDAY_INDEX));
		arguments.putParcelable(
			SUNDAY_WEATHER_ARG, weather.get(SUNDAY_INDEX));
		tripDetailFragment.setArguments(arguments);
		
		return tripDetailFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
			R.layout.fragment_tripdetail, container, false);
		
		Integer position = getArguments().getInt(POSITION_ARG);
		mVariantTitle = (TextView) rootView.findViewById(R.id.variantTitle);
		mVariantTitle.setText("Variant " + position);
		
		TripVariant variant = (TripVariant) getArguments().get(VARIANT_ARG);
		mBudgetAmount = (TextView) rootView.findViewById(R.id.detailAmount);
		mBudgetAmount.setText("$" + 
				String.format("%.2f", variant.getRemainingBudget()));
		
		Map<String, List<Event>> dayEvents = variant.getScheduleByDay();
		Weather fridayWeather = 
			(Weather) getArguments().get(FRIDAY_WEATHER_ARG);
		Weather saturdayWeather = 
			(Weather) getArguments().get(SATURDAY_WEATHER_ARG);
		Weather sundayWeather = 
			(Weather) getArguments().get(SUNDAY_WEATHER_ARG);
		
		List<Day> dayList = new ArrayList<Day>();
		dayList.add(new Day(
				"Friday",
				fridayWeather, 
				variant.getPlaces().get(FRIDAY_INDEX), 
				dayEvents.get("Friday")));
		dayList.add(new Day(
				"Saturday",
				saturdayWeather, 
				variant.getPlaces().get(SATURDAY_INDEX), 
				dayEvents.get("Saturday")));
		dayList.add(new Day(
				"Sunday",
				sundayWeather, 
				variant.getPlaces().get(SUNDAY_INDEX), 
				dayEvents.get("Sunday")));
		
		mDays = (ListView) rootView.findViewById(R.id.dayList);
		mDays.setAdapter(
			new DayDetailAdapter(rootView.getContext(), dayList));
		
		return rootView;
	}
	
	/**
	 * Helper class that holds organized information
	 * about a single day, extracted from the aggregate TripVariant
	 * in addition to the weather. a List of these classes will be
	 * passed to the DayDetailAdapter to simplify viewing
	 */
	public class Day {
		
		private String mDay;
		private Weather mDayWeather;
		private List<Place> mDayPlaces;
		private List<Event> mDayEvents;
		
		public Day(String day, Weather weather, 
				List<Place> places, List<Event> events) {
			mDay = day;
			mDayWeather = weather;
			mDayPlaces = places;
			mDayEvents = events;
		}
		
		public String getDay() {
			return mDay;
		}
		
		public Weather getWeather() {
			return mDayWeather;
		}
		
		public List<String> getPlaces() {
			return makeStringList(mDayPlaces);
		}
		
		public List<String> getEvents() {
			return makeStringList(mDayEvents);
		}
		
		private <T> List<String> makeStringList(List<T> list) {
			List<String> retList = new ArrayList<String>();
			if (list != null && !list.isEmpty()) {
				for(T item : list) {
					retList.add(item.toString());
				}
			} else {
				retList.add("No Time for Events!");
			}
			return retList;
 		}
	}
	
	private class DayDetailAdapter extends BaseAdapter {
		
		private List<Day> mData;
		private LayoutInflater mViewInflater;
		
		public DayDetailAdapter(Context context, List<Day> data) {
			mData = data;
			mViewInflater = (LayoutInflater) 
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position,
				View currentView, ViewGroup parent) {
			View newView = currentView != null ?
				currentView :
				mViewInflater.inflate(R.layout.row_day, null);
			
			Day day = mData.get(position);
	     
	        TextView dayName = 
	        	(TextView) newView.findViewById(R.id.day);
	        TextView dayWeather =
        		(TextView) newView.findViewById(R.id.dayWeather);
	        LinearLayout placeList =
        		(LinearLayout) newView.findViewById(R.id.placeList);
	        LinearLayout eventList =
        		(LinearLayout) newView.findViewById(R.id.eventList);
	        
	        dayName.setText(day.getDay());
	        dayWeather.setText(day.getWeather().toString());
	        loadLinearLayout(placeList, day.getPlaces());
	        loadLinearLayout(eventList, day.getEvents());
	        return newView;
		}
		
		@SuppressLint("InflateParams")
		private void loadLinearLayout(
				LinearLayout layout, List<String> strings) {
			if(layout.getChildCount() <= 0) {
				for(String string : strings) {
		        	View basicView = mViewInflater.inflate(
		        		R.layout.row_basicstring, null);
		        	TextView text = 
		        		(TextView) basicView.findViewById(R.id.basicString);
		        	text.setText(string);
		        	layout.addView(text);
		        }
			}
		}
	}
	
	public class BasicStringAdapter extends BaseAdapter {
		
		private List<String> mData;
		private LayoutInflater mViewInflater;
		
		public BasicStringAdapter(Context context, List<String> data) {
			mData = data;
			mViewInflater = (LayoutInflater) 
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position,
				View currentView, ViewGroup parent) {
			View newView = currentView != null ?
				currentView :
				mViewInflater.inflate(R.layout.row_basicstring, null);
			
			String basicString = mData.get(position);
			
	        TextView stringData = 
	        	(TextView) newView.findViewById(R.id.basicString);

	        stringData.setText(basicString);
	        return newView;
		}
	}
	
}
