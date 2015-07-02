package example.web.ops;

import example.web.responses.OAuth2TokenResponse;
import example.web.utils.NoAuthUtils;
import example.web.utils.BaseOAuth2Utils;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.UrlConnectionClient;

/**
 * Provides common members and functionality that
 * each class that interacts with an API probably needs
 */
public abstract class BaseOps<T> {
	
	/**
	 * The endpoint of the API being queried
	 */
	protected String mEndpoint;
	
	/**
	 * The Retrofit service that handles querying the API
	 */
	protected T mService;
	
	/**
	 * The required Authorization utilities if necessary
	 */
	protected BaseOAuth2Utils mAuthUtils;
	
	protected BaseOps(String endpoint, Class<T> serviceClass) {
		init(endpoint, serviceClass, null);
	}
	
	protected BaseOps(String endpoint, 
			Class<T> serviceClass, BaseOAuth2Utils authUtils) {
		init(endpoint, serviceClass, authUtils);
	}
	
	private void init(String endpoint, Class<T> serviceClass,
			BaseOAuth2Utils authUtils) {
		mEndpoint = endpoint;
		mService = makeService(serviceClass);
		mAuthUtils = authUtils != null ? authUtils : new NoAuthUtils();
	}
	
	public String getEndpoint() {
		return mEndpoint;
	}
	
	/**
	 * Provides the common structure for an OAuth2 token
	 * i.e. "Bearer <api-token>"
	 */
	public String getAuthToken() {
		return mAuthUtils.makeBearerToken(authorize().getAccessToken());
	}
	
	/**
	 * Allows the way that each API is authorized to be tailored
	 * to the specific API
	 */
	protected abstract OAuth2TokenResponse authorize();
	
	public BaseOAuth2Utils getAuthUtils() {
		return mAuthUtils;
	}
	
	public T getService() {
		return mService;
	}
	
	private T makeService(Class<T> serviceClass) {
		return new RestAdapter.Builder()
			.setClient(new UrlConnectionClient())
			.setEndpoint(mEndpoint)
			//.setLogLevel(LogLevel.FULL)
			.build()
			.create(serviceClass);
	}
	
	/**
	 * Helper method to identify the execution order of the pipeline
	 * operations
	 */
	protected void logExecutionTime(String method) {
		System.out.println(method + " - "
			+ System.currentTimeMillis() % 100000);
	}

}
