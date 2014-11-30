package example.imagetaskgang;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @class MainActivity
 *
 * @brief Main Activity for the Android version of the ImageTaskGang
 *        application.
 */
public class MainActivity extends Activity {
    /**
     * A LinearLayout where each element is an AutoCompleteTextview
     * that holds a comma-separated list of URLs to download.
     */
    protected LinearLayout mListUrlGroups;
	
    /**
     * Suggestions of default URLs that are supposed to be presented
     * to the user via AutoCompleteTextView.
     */
    private final String[] SUGGESTIONS = new String[] {        
        "http://www.mariowiki.com/images/thumb/1/19/GoldMushroomNSMB2.png/200px-GoldMushroomNSMB2.png,"
        + "http://png-1.findicons.com/files/icons/2297/super_mario/256/mushroom_life.png",
        "http://img4.wikia.nocookie.net/__cb20080812195802/nintendo/en/images/1/12/1upshroom.png,"
        + "http://www.mariowiki.com/images/thumb/5/57/Powerup-mini-mushroom-sm.png/200px-Powerup-mini-mushroom-sm.png,"
        + "http://a66c7b.medialib.glogster.com/media/92/92a90af3755a6e3de9faad540af216bc3cdd7839add09a7735c22844b725d55b/propeller-mushroom-jpg.jpg"
    };
    
    /**
     * The adapter responsible for recommending suggestions of URLs to
     * download images from.
     */
    private ArrayAdapter<String> mSuggestions;
	
    /**
     * Array of Filters to apply to the downloaded images.
     */
    private final Filter[] FILTERS = {
        new NullFilter(),
        new GrayScaleFilter()
    };
	
    /**
     * The name of the extra attached to the intent that starts
     * ResultActivity, which allows the ResultActivity to divide the
     * output into groups for viewing the results more clearly.
     */
    static final String FILTER_EXTRA = "filter_extra";

    /**
     * Define a completion hook that's called back to display the
     * results after the ImageTaskGang finishes downloading,
     * processing, and storing Images provided by the List of URLs.
     */
    final Runnable displayResultsRunnable = 
        new Runnable() {
            @Override
                public void run() {
                // Run the displayResults() method on the UI Thread so
                // that startActivity() occurs in that context.
                MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                            public void run() {
                            setButtonsEnabled(true);
                            displayResults();
                        }
                    });
            }
        };

    /**
     * Hook method called when the Activity is first launched to
     * initialize the content view and various data members.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the main view.
        setContentView(R.layout.activity_main);
		
        // Cache a LinearLayout where each element is an
        // AutoCompleteTextview that holds a comma-separated list of
        // URLs to download.
        mListUrlGroups =
            (LinearLayout) findViewById(R.id.listOfURLLists);

        // Initialize the Platform singleton with the appropriate
        // Platform strategy, which in this case will be the
        // AndroidPlatform.
        PlatformStrategy.instance
            (new PlatformStrategyFactory(this)
             .makePlatformStrategy());

        // Initialize the Options singleton.
        Options.instance().parseArgs(null);
    }
    
    /**
     * Hook method that is called first when the activity context has
     * been created.  Set the mSuggestions in onStart() so we are sure
     * the context exists and will not throw a NullPointerException.
     */
    @Override
    protected void onStart() {
    	super.onStart();
        mSuggestions = 
            new ArrayAdapter<String>(this,
                                     R.layout.suggestion_item,
                                     SUGGESTIONS);
    }
	
    /**
     * Adds a List of URLs to the ListView to allow for variable
     * number of URL Lists to process (i.e., variable number of
     * iteration cycles by the ImageTaskGang).
     */
    @SuppressLint("InflateParams")
    public void addURLs(View view) {
        AutoCompleteTextView newList = 
            (AutoCompleteTextView) 
            LayoutInflater.from(this).inflate (R.layout.list_item,
                                               null);
        newList.setAdapter(mSuggestions);
        mListUrlGroups.addView(newList);
        mListUrlGroups.invalidate();
    }
	
    /**
     * Run the gang using a default set of URL lists hardcoded into
     * the application rather than reading the input lists.
     */
    public void useDefaultURLs(View view) {
        new Thread(new ImageTaskGang(FILTERS,
                                     PlatformStrategy.instance().getUrlIterator
                                     (PlatformStrategy.InputSource.DEFAULT),
                                     displayResultsRunnable)).start();
        setButtonsEnabled(false);
    }
	
    /**
     * Run the gang by reading the input lists of URLs.
     */
    public void runImageTaskGang(View view) {
    	Iterator<List<URL>> iterator = 
            PlatformStrategy.instance().getUrlIterator
            (PlatformStrategy.InputSource.USER);
    	
    	// Check to see if the user entered any lists.
    	if (iterator != null) {
            if (iterator.hasNext() && !isEmpty()) {
                new Thread(new ImageTaskGang(FILTERS,
                                             iterator,
                                             displayResultsRunnable)).start();
                setButtonsEnabled(false);
            } else 
                showToast("No list of URLs entered");
    	}
    }
    
    /**
     * Checks to see if all the lists are empty.
     */
    private boolean isEmpty() {
    	int listCount = mListUrlGroups.getChildCount();
    	for (int i = 0; i < listCount; ++i) {
            AutoCompleteTextView list = 
                (AutoCompleteTextView) mListUrlGroups.getChildAt(i);
            if (list.getText().length() > 0) 
                return false;
    	}
    	return true;
    }

    /**
     * Sets the list of buttons to disabled, which shows that the
     * application is processing visually, and is a (slightly
     * forceful) way to keep multiple task gangs from being started
     * accidentally.
     */
    private void setButtonsEnabled(boolean enabled) {
    	LinearLayout buttonLayout = 
            (LinearLayout) findViewById(R.id.buttonLayout);
    	int buttonCount = buttonLayout.getChildCount();
    	for (int i = 0; i < buttonCount; ++i) {
            buttonLayout.getChildAt(i).setEnabled(enabled);
    	}
    }
	
    /**
     * Delete the previously downloaded pictures and directories.
     */
    public void clearFilterDirectories(View view) {
    	setButtonsEnabled(false);
        for (Filter filter : FILTERS) {
            deleteSubFolders
                (new File(PlatformStrategy.instance().getDirectoryPath(), 
                          filter.getName()).getAbsolutePath());
        }
        setButtonsEnabled(true);
        showToast("Previously downloaded files deleted");
    }
	
    /**
     * A helper method that recursively deletes files in a specified
     * directory. 
     */
    private void deleteSubFolders(String path) {
        File currentFolder = new File(path);        
        File files[] = currentFolder.listFiles();

        if (files == null) 
            return;

        // Android does not allow you to delete a directory with child
        // files, so we need to write code that handles this
        // recursively.
        for (File f : files) {          
            if (f.isDirectory()) 
                deleteSubFolders(f.toString());
            f.delete();
        }
        currentFolder.delete();
    }
	
    /**
     * Creates an Intent that's used to start the ResultsActivity,
     * which can be used to view the results.
     */
    private void displayResults() {
        // Pass a list of filterNames to the ResultsActivity so it
    	// knows what buttons to generate to allow the user to view
    	// all the downloaded results.
        String[] filterNames =
            new String[FILTERS.length];

        for (int i = 0; i < filterNames.length; ++i) 
            filterNames[i] = FILTERS[i].getName();
        
        // Create the intent and add the list of filterNames as an
        // extra.
        Intent resultsIntent =
            new Intent(this,
                       ResultsActivity.class);
        resultsIntent.putExtra(FILTER_EXTRA, 
                               filterNames);
        // Start the ResultsActivity.
        startActivity(resultsIntent);
    }
    
    /**
     * Show a toast to the user.
     */
    public void showToast(String msg) {
        Toast.makeText(this,
                       msg,
                       Toast.LENGTH_SHORT).show();
    }
}
