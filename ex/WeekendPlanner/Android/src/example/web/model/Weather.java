package example.web.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO that includes the relevant fields of the
 * Weather object from the OpenWeatherMap API necessary
 * to the WeekendResponse
 */
public class Weather implements Parcelable {
	
	@SerializedName("temp")
	private Temperature mTemperature;
	
	@SerializedName("weather")
	private List<Condition> mConditions;
	
	@SuppressWarnings("unchecked")
	public Weather(Parcel source) {
		mTemperature = (Temperature) source.readValue(null);
		mConditions = (List<Condition>) source.readValue(null);
	}
	
	public Double getDayTemperature() {
		return mTemperature.getDayTemperature();
	}
	
	public String getDayCondition() {
		return mConditions.get(0).getCondition();
	}
	
	@Override
	public String toString() {
		return mTemperature + "F and " + getDayCondition();
	}
	
	/**
	 * Required by the Parcelable interface within the Android
	 * framework.
	 */
	@Override
	public int describeContents() {
		// No special contents (i.e. FileDescriptors)
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mTemperature);
		dest.writeValue(mConditions);
	}
	
	public static final Parcelable.Creator<Weather>
			CREATOR = new Creator<Weather>() {
		@Override
		public Weather createFromParcel(Parcel source) {
			return new Weather(source);
		}
		
		@Override
		public Weather[] newArray(int size) {
		    return new Weather[size];
		}
	};

	/**
	 * A nested POJO upon which the Weather object relies
	 */
	public class Temperature implements Parcelable {
		
		@SerializedName("day")
		private Double mDayTemperature;
		
		public Temperature(Parcel source) {
			mDayTemperature = source.readDouble();
		}
		
		public Double getDayTemperature() {
			return mDayTemperature;
		}
		
		public String toString() {
			return mDayTemperature.toString();
		}
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeDouble(mDayTemperature);
		}
		
		public final Parcelable.Creator<Temperature>
				TEMP_CREATOR = new Creator<Temperature>() {
			@Override
			public Temperature createFromParcel(Parcel source) {
				return new Temperature(source);
			}
			
			@Override
			public Temperature[] newArray(int size) {
			    return new Temperature[size];
			}
		};
	}
	
	/**
	 * A nested POJO upon which the Weather object relies
	 */
	public class Condition implements Parcelable {
		
		@SerializedName("main")
		private String mCondition;
		
		public Condition(Parcel source) {
			mCondition = source.readString();
		}
		
		public String getCondition() {
			return mCondition;
		}
		
		public String toString() {
			return mCondition;
		}
		
		/**
		 * Required by the Parcelable interface within the Android
		 * framework.
		 */
		@Override
		public int describeContents() {
			// No special contents (i.e. FileDescriptors)
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mCondition);
		}
		
		public final Parcelable.Creator<Condition>
				CONDITION_CREATOR = new Creator<Condition>() {
			@Override
			public Condition createFromParcel(Parcel source) {
				return new Condition(source);
			}
			
			@Override
			public Condition[] newArray(int size) {
			    return new Condition[size];
			}
		};
	}
	
}
