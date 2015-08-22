package example.web.utils;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

import com.squareup.okhttp.OkHttpClient;

import example.web.services.WeekendPlannerService;

public class RetrofitAdapterUtils {

	public static WeekendPlannerService makeWeekendPlannerService() {
		OkHttpClient client = new OkHttpClient();
		RestAdapter weekendPlannerAdapter =
    		new RestAdapter.Builder()
				.setClient(new OkClient(client))
				.setLogLevel(LogLevel.FULL)
				.setLog(new AndroidLog("MYREQUESTS"))
				.setEndpoint("http://10.0.3.2:8080/WeekendPlanner/")
				.build();
		return weekendPlannerAdapter.create(WeekendPlannerService.class);
	}
}
