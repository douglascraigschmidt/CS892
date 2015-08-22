package example.web.utils;

/**
 * A "no-op" class For APIs with no authorization requirements
 */
public class NoAuthUtils extends BaseOAuth2Utils {
	
	{
		mClientKey = null;
		mClientSecret = null;
		mApplicationKey = null;
		mIsPreEncoded = null;
		mGrantType = null;
		mUsername = null;
		mPassword = null;
		mScope = null;
	}

}
