package example.fragments;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit.RetrofitError;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import example.web.model.City;
import example.web.requests.WeekendPlannerRequest;
import example.web.responses.CityResponse;
import example.web.responses.WeekendPlannerResponse;
import example.web.services.WeekendPlannerService;
import example.web.utils.RetrofitAdapterUtils;
import example.weekendizer.R;

/**
 * The Fragment responsible for initially prompting the user
 * for their current city and a budget
 * TODO: deduce location based on GPS
 */
public class PromptFragment extends Fragment
						    implements OnClickListener {
	
	/**
	 * A tag by which to find the Fragment in the stack
	 */
	public static final String FRAGMENT_TAG = "prompt_fragment";

	/**
	 * Fragment manager to swap out fragments 
	 */
	private FragmentManager mFragmentManager;
	
	/**
	 * The CityResponse that this fragment was created with
	 */
	private CityResponse mResults;
	
	/**
	 * Dialog indicating the server is working
	 */
	private AlertDialog mAlertDialog;
	
	/**
	 * Cached references to relevant UI widgets
	 */
	private Spinner mOriginCityPrompt;
	private Spinner mDestinationCityPrompt;
	private ArrayAdapter<String> mCityAdapter;
	private LinearLayout mDestinationLayout;
	private EditText mBudgetPrompt;
	private Button mEnterDestinationButton;
	private Button mRemoveDestinationButton;
	private Button mWeekendizeButton;
	
	/**
	 * Retrofit service communicate with our server
	 */
	private WeekendPlannerService mWeekendPlannerService;
	
	/**
	 * A map of City names to IATA Codes for airport identification
	 */
	private Map<String, City> mCities;
	
	/**
	 * The name for the argument with which this fragment has been started
	 */
	public static final String CITY_ARG = "cities";
	
	/**
	 * Factory method that is commonly accepted as the way to
	 * instantiate Fragments.
	 */
	public static PromptFragment newInstance(CityResponse response) {
		// This fragment needs no parameters for instantiation,
		// so simply return a new PromptFragment
		PromptFragment promptFragment = new PromptFragment();
		
		Bundle arguments = new Bundle();
		arguments.putParcelable(CITY_ARG, response);
		promptFragment.setArguments(arguments);
		
		return promptFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);

        mFragmentManager = getFragmentManager();
        
        mResults = (CityResponse) getArguments().get(CITY_ARG);
        
        mAlertDialog = 
			new AlertDialog.Builder(getActivity()).create();

		mOriginCityPrompt =
			(Spinner) rootView.findViewById(R.id.originCityPrompt);
		
		mDestinationCityPrompt = 
			(Spinner) rootView.findViewById(R.id.destinationCityPrompt);
		
		mDestinationLayout =
			(LinearLayout) rootView.findViewById(R.id.destinationCity);
		
		mBudgetPrompt = 
			(EditText) rootView.findViewById(R.id.budgetPrompt);
		
		mEnterDestinationButton =
			(Button) rootView.findViewById(R.id.enterDestination);
		mEnterDestinationButton.setOnClickListener(this);
		
		mRemoveDestinationButton =
			(Button) rootView.findViewById(R.id.removeDestination);
		mRemoveDestinationButton.setOnClickListener(this);
		
		mWeekendizeButton =
			(Button) rootView.findViewById(R.id.weekendize);
		mWeekendizeButton.setOnClickListener(this);

        mWeekendPlannerService =
        	RetrofitAdapterUtils.makeWeekendPlannerService();
        
    	mCities = new HashMap<String, City>();
    	
    	for(City city : mResults.getCities()) {
			mCities.put(city.getName(), city);
		}
		
		mCityAdapter =
			new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_spinner_item,
				mCities.keySet().toArray(
					new String[mCities.size()]));
		mCityAdapter.setDropDownViewResource(
			android.R.layout.simple_spinner_dropdown_item);
		
		mOriginCityPrompt.setAdapter(mCityAdapter);
		mDestinationCityPrompt.setAdapter(mCityAdapter);
        
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.enterDestination:
		case R.id.removeDestination:
			toggleVisibility();
			break;
		case R.id.weekendize:
			if(isValidInput())
				new WeekendPlannerTask().execute();
			break;
		}
	}
	
	private void toggleVisibility() {
		mDestinationLayout.setVisibility(
			mDestinationLayout.getVisibility() == View.GONE ?
				View.VISIBLE : View.GONE);
		
		mEnterDestinationButton.setVisibility(
			mEnterDestinationButton.getVisibility() == View.GONE ?
				View.VISIBLE : View.GONE);
		
		mRemoveDestinationButton.setVisibility(
			mRemoveDestinationButton.getVisibility() == View.GONE ?
				View.VISIBLE : View.GONE);
	}
	
	private Boolean isValidInput() {
		String budget = mBudgetPrompt.getText().toString();
	
		try {
			if(Double.valueOf(budget).equals(null)) {
				showToast("Invalid Budget");
				return false;
			}
		} catch(NumberFormatException e) {
			showToast("Invalid Budget");
			return false;
		}
		
		return true;
	}
    
    private class WeekendPlannerTask
    	extends AsyncTask<Void, Void, WeekendPlannerResponse> {
    	
    	@Override
    	protected void onPreExecute() {
    		showDialog("Weekendizing",
    				   "Please wait while your weekend is being planned");
    	}

		@Override
		protected WeekendPlannerResponse doInBackground(Void... params) {
			WeekendPlannerResponse resp = new WeekendPlannerResponse();
			try {
				resp = mWeekendPlannerService.weekendize(buildRequest());
			} catch (RetrofitError e) {
				resp.setError(e.getMessage() 
					+ " " + e.getBodyAs(String.class));
			}
			return resp;
		}
		
		@Override
		protected void onPostExecute(WeekendPlannerResponse response) {
			if (response.getError() != null) {
				showToast(response.getError());
			} else {
				mFragmentManager
					.beginTransaction()
					.replace(
						R.id.container, 
						ResultsFragment.newInstance(response), 
						ResultsFragment.FRAGMENT_TAG)
					.addToBackStack(ResultsFragment.FRAGMENT_TAG)
					.commit();
			}
			mAlertDialog.cancel();
		}
		
		// assumes input has been verified
		private WeekendPlannerRequest buildRequest() {
			return new WeekendPlannerRequest(
				getBudget(),
				getOrigin(),
				getDestination());
		}
		
		private String getBudget() {
			return mBudgetPrompt.getText().toString();
		}
		
		private City getOrigin() {
			return mCities.get(
				mOriginCityPrompt.getSelectedItem().toString());
		}
		
		private City getDestination() {
			return mDestinationLayout.getVisibility() == View.GONE ?
				mCities.get(
					mDestinationCityPrompt.getItemAtPosition(
						new Random().nextInt(mCities.size())).toString())
				: mCities.get(
					mDestinationCityPrompt.getSelectedItem().toString());
		}
    }
    
    /**
     * Shows a dialog to the user, indicating that a long
     * running background operation is in progress
     */
    private void showDialog(String title, String msg) {
    	mAlertDialog.setTitle(title);
		mAlertDialog.setMessage(msg);
		mAlertDialog.show();
    }
    
    /**
     * Show a toast to the user.
     */
    private void showToast(String msg) {
        Toast.makeText(this.getActivity(),
                       msg,
                       Toast.LENGTH_LONG).show();
    }
    
}
