package example.web.model;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO with the relevant fields of a City object from
 * the Sabre Flights API
 */
public class City {
	@SerializedName("code")
	private String mCode;
	
	@SerializedName("name")
	private String mName;
	
	public String getCode() {
		return mCode;
	}
	
	public String getName() {
		return mName;
	}
	
}
