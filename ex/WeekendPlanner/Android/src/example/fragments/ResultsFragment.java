package example.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import example.web.model.TripVariant;
import example.web.responses.WeekendPlannerResponse;
import example.weekendizer.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsFragment extends Fragment {
	
	/**
	 * A tag by which to find the Fragment in the stack
	 */
	public static final String FRAGMENT_TAG = "results_fragment";
	
	/**
	 * Fragment manager for handling transitions
	 */
	private FragmentManager mFragmentManager;
	
	/**
	 * The WeekendPlannerResponse that this fragment
	 * displays
	 */
	private WeekendPlannerResponse mResults;
	
	/**
	 * Relevant UI widgets
	 */
	private TextView mOriginCity;
	private TextView mDestinationCity;
	private TextView mDepartingDate;
	private TextView mReturningDate;
	private TextView mInitBudget;
	private TextView mFlightCost;
	private TextView mDepartingInfo;
	private TextView mReturningInfo;
	private ListView mTripVariants;
	
	/**
	 * The name for the argument with which this fragment has been started
	 */
	public static final String RESULTS_ARG = "results";
	
	/**
	 * Creates a new instance of the ResultsFragment meant
	 * to display the results of the parameter response
	 */
	public static ResultsFragment newInstance(
			WeekendPlannerResponse response) {
		ResultsFragment resultsFragment = new ResultsFragment();
		
		Bundle arguments = new Bundle();
		arguments.putParcelable(RESULTS_ARG, response);
		resultsFragment.setArguments(arguments);
		
		return resultsFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_results, container,
				false);
		
		mFragmentManager = getFragmentManager();
		
		mResults = (WeekendPlannerResponse) getArguments().get(RESULTS_ARG);
		
		mOriginCity = (TextView) rootView.findViewById(R.id.originCity);
		mDestinationCity = (TextView) rootView.findViewById(R.id.destinationCity);
		mDepartingDate = (TextView) rootView.findViewById(R.id.departingDate);
		mReturningDate = (TextView) rootView.findViewById(R.id.returningDate);
		mInitBudget = (TextView) rootView.findViewById(R.id.initBudget);
		mFlightCost = (TextView) rootView.findViewById(R.id.flightCost);
		mDepartingInfo = (TextView) rootView.findViewById(R.id.departingInfo);
		mReturningInfo = (TextView) rootView.findViewById(R.id.returningInfo);
		mTripVariants = (ListView) rootView.findViewById(R.id.tripVariants);
		
		mOriginCity.setText(mResults.getOriginCityName());
		mDestinationCity.setText(mResults.getDestinationCityName());
		
		Date departingDeparture = null;
		Date returningDeparture = null;
		SimpleDateFormat formatter = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		
		try {
			departingDeparture = 
				formatter.parse(mResults.getDepartingDepartureDateTime());
			returningDeparture =
				formatter.parse(mResults.getReturningDepartureDateTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		mDepartingDate.setText(
			SimpleDateFormat.getDateTimeInstance().format(departingDeparture));
		mReturningDate.setText(
			SimpleDateFormat.getDateTimeInstance().format(returningDeparture));
		
		mInitBudget.setText("$" +
			String.format("%.2f", mResults.getInitialBudget()));
		mFlightCost.setText("$" +
			String.format("%.2f", mResults.getFlight().getFare()));
		
		mDepartingInfo.setText(
			mResults.getFlight().getDepartingDepartureInfo());
		mReturningInfo.setText(
			mResults.getFlight().getReturningDepartureInfo());
		
		mTripVariants.setAdapter(
			new TripVariantAdapter(
				rootView.getContext(), mResults.getTripVariants()));
		
		return rootView;
	}
	
	private class TripVariantAdapter extends BaseAdapter {
		
		private List<TripVariant> mData;
		private LayoutInflater mViewInflater;
		
		public TripVariantAdapter(Context context, List<TripVariant> data) {
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
				mViewInflater.inflate(R.layout.row_tripvariant, null);
			
			TripVariant variant = mData.get(position);
	     
	        TextView variantName = 
	        	(TextView) newView.findViewById(R.id.variantName);
	        TextView remainingAmount =
	        		(TextView) newView.findViewById(R.id.remainingAmount);
	        TextView ticketCategories =
	        		(TextView) newView.findViewById(R.id.ticketCategories);
	        TextView placeTypes =
	        		(TextView) newView.findViewById(R.id.placeTypes);
	        
	        variantName.setText("Variant " + (position + 1));
	        remainingAmount.setText("$" +
	        	String.format("%.2f", variant.getRemainingBudget()));
	        ticketCategories.setText(variant.getCategories().toString());
	        placeTypes.setText(variant.getTypes().toString());
	        
	        newView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mFragmentManager
						.beginTransaction()
						.replace(
							R.id.container, 
							TripDetailFragment
								.newInstance(
									position + 1,
									mData.get(position),
									mResults.getWeather()),
							TripDetailFragment.FRAGMENT_TAG)
						.addToBackStack(TripDetailFragment.FRAGMENT_TAG)
						.commit();
				}
	        });
	        return newView;
		}
	}

}
