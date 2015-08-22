package example.web.responses;

import com.google.gson.annotations.SerializedName;

/**
 * The top-level POJO returned commonly by OAuth2 requests
 */
public class OAuth2TokenResponse {
	
	/**
	 * The access credential returned by the server
	 */
	@SerializedName("access_token")
	private String mAccessToken;
	
	/**
	 * The token type
	 */
	@SerializedName("token_type")
	private String mTokenType;
	
	/**
	 * The life span of the token
	 */
	@SerializedName("expires_in")
	private String mExpiresIn;
	
	public OAuth2TokenResponse(String credential) {
		mAccessToken = credential;
	}
	
	public String getAccessToken() {
		return mAccessToken;
	}
	
	public String toString() {
		return mAccessToken
				+ " {type: " + mTokenType
				+ ", expires: " + mExpiresIn + "}";
	}
	
}
