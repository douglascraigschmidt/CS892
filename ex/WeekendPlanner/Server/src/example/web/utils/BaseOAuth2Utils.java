package example.web.utils;

import java.util.Base64;
import java.util.Base64.Encoder;

/**
 * Utility base class to provide common functionality needed to
 * interact with many different APIs that implement the OAuth2
 * authorization protocol with various API-specific details
 */
public abstract class BaseOAuth2Utils {
	
	private final static Encoder mBase64Encoder = Base64.getEncoder();
	public final static int APP_TOKEN = 0;
	public final static int USER_TOKEN = 1;
	
	/**
	 * Commonly used parameters and request headers for 
	 * interacting with an OAuth2 protected API
	 */
	protected String mClientKey;
	protected String mClientSecret;
	protected String mApplicationKey;
	protected Boolean mIsPreEncoded;
	protected String mGrantType;
	protected String mUsername;
	protected String mPassword;
	protected String mScope;
	
	/**
	 * Makes a credential based on the type of token needed
	 * and whether the provided credential is already base64 encoded
	 */
	public String makeCredential(Integer tokenType) {
		switch(tokenType) {
		case APP_TOKEN:
			return encodeIfNecessary(mApplicationKey);
		case USER_TOKEN:
			String cred =
				encodeIfNecessary(mClientKey)
				+ ":"
				+ encodeIfNecessary(mClientSecret);
			
			return "Basic " + mBase64Encoder.encodeToString(cred.getBytes());
		default:
			// throw tokenTypeNotSupported Exception
			return null;
		}
	}
	
	private String encodeIfNecessary(String target) {
		return !mIsPreEncoded ? 
			mBase64Encoder.encodeToString(target.getBytes())
			: target;
	}
	
	public String makeBearerToken(String authToken) {
		return "Bearer " + authToken;
	}
	
	public String getGrantType() {
		return mGrantType;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	public String getScope() {
		return mScope;
	}

}
