package example.weekendizer;

import retrofit.RetrofitError;
import example.fragments.PromptFragment;
import example.web.responses.CityResponse;
import example.web.services.WeekendPlannerService;
import example.web.utils.RetrofitAdapterUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	/**
	 * Dialog indicating the server is working
	 */
	private AlertDialog mAlertDialog;
	
	/**
	 * Retrofit service communicate with our server
	 */
	private WeekendPlannerService mWeekendPlannerService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAlertDialog = new AlertDialog.Builder(this).create();
        
        mWeekendPlannerService =
        	RetrofitAdapterUtils.makeWeekendPlannerService();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Retrieve the available cities from the Sabre Flights API
		// and pass them to the PromptFragment
		new CityInfoTask().execute();
	}
	
	private class CityInfoTask 
		extends AsyncTask<Void, Void, CityResponse> {
		
		private String mCountry;
		private final String DEFAULT_COUNTRY = "US";
		
		public CityInfoTask() {
			mCountry = DEFAULT_COUNTRY;
		}
	 	
		@SuppressWarnings("unused")
		public CityInfoTask(String country) {
			mCountry = country;
		}
		
		@Override
		protected void onPreExecute() {
			showDialog("Gathering City Info",
					   	   "Finding eligible cities for your country");
		}
	
		@Override
		protected CityResponse doInBackground(Void... params) {
			CityResponse resp = new CityResponse();
			try {
				resp = mWeekendPlannerService.queryCities(mCountry);
			} catch (RetrofitError e) {
				resp.setError(e.getMessage() 
					+ " " + e.getBodyAs(String.class));
			}
			return resp;
		}
		
		@Override
		protected void onPostExecute(CityResponse response) {
			if (response.getError() != null) {
				showToast(response.getError());
			} else {
				getFragmentManager().beginTransaction()
					.replace(
						R.id.container,
						PromptFragment.newInstance(response),
						PromptFragment.FRAGMENT_TAG)
					.commit();
			}	
			mAlertDialog.cancel();
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
        Toast.makeText(
        	this, msg, Toast.LENGTH_LONG).show();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
