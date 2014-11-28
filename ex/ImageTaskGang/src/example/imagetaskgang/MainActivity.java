package example.imagetaskgang;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @class MainActivity
 *
 * @brief Main Activity for the Android version of the ImageTaskGang
 *        application.
 */
public class MainActivity extends Activity {
    /**
     * A ListView where each element holds a
     * comma-separated list of URLs to download
     */
    private ListView mListUrlGroups;

    /**
     * The Adapter that updates the list of
     * comma-separated lists of URLs
     */
    private URLListAdapter mURLListAdapter;
	
    /**
     * Suggestions of default URLs that are supposed to be
     * presented to the user via AutoCompleteTextView. Currently
     * broken
     */
    private final String[] SUGGESTIONS = new String[] {        
        "http://www.mariowiki.com/images/thumb/1/19/GoldMushroomNSMB2.png/200px-GoldMushroomNSMB2.png",
        "http://png-1.findicons.com/files/icons/2297/super_mario/256/mushroom_life.png",
        "http://img4.wikia.nocookie.net/__cb20080812195802/nintendo/en/images/1/12/1upshroom.png",
        "http://www.mariowiki.com/images/thumb/5/57/Powerup-mini-mushroom-sm.png/200px-Powerup-mini-mushroom-sm.png",
        "http://a66c7b.medialib.glogster.com/media/92/92a90af3755a6e3de9faad540af216bc3cdd7839add09a7735c22844b725d55b/propeller-mushroom-jpg.jpg"
    };
	
    /**
     * Array of Filters to apply to the downloaded images.
     */
    private final Filter[] FILTERS = {
        new NullFilter(),
        new GrayScaleFilter(),
        new NullFilter("Null1"),
        new NullFilter("Null2"),
        new GrayScaleFilter("Gray1"),
        new GrayScaleFilter("Gray2")
    };
	
    /**
     * The name of the extra attached to the intent that
     * starts ResultActivity. This allows the ResultActivity
     * to divide the output into groups for viewing the results 
     * more clearly.
     */
    static final String FILTER_EXTRA = "filter_extra";

    /**
     * Define a completion hook that's called back when the
     * ImageTaskGang is finished to display the results.
     */
    final Runnable displayResultsRunnable = 
        new Runnable() {
            @Override
            public void run() {
                // @@ Nolan, I don't think we need to run the
                // displayResults() method on the UI Thread since
                // startActivity() can be called from a background
                // Thread.  Can you please experiment with this a bit
                // to see if we can zap the call to runOnUiThread()
                // here?
                MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
        setContentView(R.layout.activity_main);
		
        mListUrlGroups = (ListView) findViewById(R.id.listOfURLLists);
        mListUrlGroups.setItemsCanFocus(true);
        mURLListAdapter = new URLListAdapter();
        mListUrlGroups.setAdapter(mURLListAdapter);

        // Initializes the Platform singleton with the appropriate
        // Platform strategy, which in this case will be the
        // AndroidPlatform.
        PlatformStrategy.instance
            (new PlatformStrategyFactory(this)
             .makePlatformStrategy());

        // Initializes the Options singleton.
        Options.instance().parseArgs(null);
    }
	
    /**
     * Adds a list to the ListView to allow for variable
     * number of lists to process (i.e. variable number
     * of cycles in the TaskGang).
     */
    public void addList(View view) {
        mURLListAdapter.addList();
        mURLListAdapter.notifyDataSetChanged();
    }
	
    /**
     * Run the gang using a default set of URL lists 
     * hardcoded into the application rather than 
     * reading the input lists
     */
    public void useDefault(View view) {
        new Thread(new ImageTaskGang(FILTERS,
                                     getURLIterator(),
                                     displayResultsRunnable)).start();
    }
	
    /**
     * Run the gang by reading the input lists of URLs
     */
    public void runGang(View view) {
        new Thread(new ImageTaskGang(FILTERS,
                                     mURLListAdapter.getURLIterator(),
                                     new Runnable() { 
                                         @Override
                                         public void run() {
                                             displayResults();
                                         }
                                     })).start();
    }
	
    /**
     * Delete the previously downloaded pictures and directories
     */
    public void clearFilterDirectories(View view) {
        for (Filter filter : FILTERS) {
            deleteSubFolders(new File(PlatformStrategy.instance().getDirectoryPath(), 
                                      filter.getName()).getAbsolutePath());
        }
    }
	
    /**
     * A helper method that recursively deletes files in a 
     * specified directory. Android does not allow you to
     * delete a directory with child files.
     */
    private void deleteSubFolders(String path) {
        File currentFolder = new File(path);        
        File files[] = currentFolder.listFiles();

        if (files == null) {
            return;
        }
        for (File f : files) {          
            if (f.isDirectory()) {
                deleteSubFolders(f.toString());
            }
            f.delete();
        }
    }
	
    /**
     * Starts the intent to view the results via ResultsActivity
     */
    private void displayResults() {
        // Pass a list of filterNames to the ResultsActivity
    	// so it knows what buttons to generate to allow
    	// the user to view all the downloaded results.
        String[] filterNames = new String[FILTERS.length];
        for (int i = 0; i < filterNames.length; ++i) {
            filterNames[i] = FILTERS[i].getName();
        }
        
        // Create the intent and add the list of filterNames as an extra
        Intent resultsIntent = new Intent(this,
                                          ResultsActivity.class);
        resultsIntent.putExtra(FILTER_EXTRA, 
                               filterNames);
        
        // Start the ResultsActivity
        startActivity(resultsIntent);
    }
	
    /**
     * Get the input from the default array of arrays.
     * @throws IOException 
     */
    private Iterator<List<URL>> getURLIterator() {
    	try {
            final URL[] urls1 = {        
                new URL("http://www.mariowiki.com/images/thumb/1/19/GoldMushroomNSMB2.png/200px-GoldMushroomNSMB2.png"),
                new URL("http://png-1.findicons.com/files/icons/2297/super_mario/256/mushroom_life.png")
            };
            final URL[] urls2 = {
                new URL("http://img4.wikia.nocookie.net/__cb20080812195802/nintendo/en/images/1/12/1upshroom.png"),
                new URL("http://www.mariowiki.com/images/thumb/5/57/Powerup-mini-mushroom-sm.png/200px-Powerup-mini-mushroom-sm.png"),
                new URL("http://a66c7b.medialib.glogster.com/media/92/92a90af3755a6e3de9faad540af216bc3cdd7839add09a7735c22844b725d55b/propeller-mushroom-jpg.jpg")
            };
            final List<List<URL>> variableNumberOfInputURLs = 
                new ArrayList<List<URL>>();
            variableNumberOfInputURLs.add(Arrays.asList(urls1));
            variableNumberOfInputURLs.add(Arrays.asList(urls2));
            return variableNumberOfInputURLs.iterator();
    	} catch (MalformedURLException e) {
            return null;
    	}   
    }

    /**
     * @class URLListAdapter
     *
     * @brief @@ Nolan, please fill in here.
     */
    public class URLListAdapter extends BaseAdapter {
        /**
         * @@ Nolan, please fill in here.
         */
        private LayoutInflater mInflater;

        /**
         * @@ Nolan, please fill in here.
         */
        private List<AutoCompleteTextView> mList;

        /**
         * @@ Nolan, please fill in here.
         */
        private ArrayAdapter<String> mSuggestions = 
            new ArrayAdapter<String>(getApplicationContext(),
                                     R.layout.suggestion_item, SUGGESTIONS);
		
        /**
         * @@ Nolan, please fill in here.
         */
        public URLListAdapter() {
            mInflater = (LayoutInflater) 
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            AutoCompleteTextView initView = 
                new AutoCompleteTextView(getApplicationContext());
            initView.setAdapter(mSuggestions);
            initView.setDropDownHeight(initView.getHeight());
            initView.setDropDownWidth(initView.getWidth());
            mList = new ArrayList<AutoCompleteTextView>(Arrays.asList(initView));
            notifyDataSetChanged();
        }
		
        /**
         * @@ Nolan, please fill in here.
         */
        public void addList() {
            AutoCompleteTextView addView = 
                new AutoCompleteTextView(getApplicationContext());
            addView.setAdapter(mSuggestions);
            mList.add(addView);
            notifyDataSetChanged();
        }

        /**
         * @@ Nolan, please fill in here.
         */
        public Iterator<List<URL>> getURLIterator() {
            List<List<URL>> urlLists = new ArrayList<List<URL>>();
            for (AutoCompleteTextView actView : mList) {
                List<URL> urls = new ArrayList<URL>();
                StringTokenizer tokenizer = 
                    new StringTokenizer(actView.getText().toString(), ", ");
                while (tokenizer.hasMoreTokens()) {
                    try {
                        urls.add(new URL(tokenizer.nextToken().trim()));
                    } catch (MalformedURLException e) {
                        // @@ show a toast?
                        e.printStackTrace();
                    }
                }
                urlLists.add(urls);
            }
            return urlLists.iterator();
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
            public int getCount() {
            return mList.size();
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
            public AutoCompleteTextView getItem(int arg0) {
            return mList.get(arg0);
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
            public long getItemId(int arg0) {
            return arg0;
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @SuppressLint("InflateParams")
            @Override
            public View getView(final int position, View activeView, 
				ViewGroup parent) {
            AutoCompleteTextView newView;
            if (activeView == null) {
                activeView = mInflater.inflate(R.layout.list_item, null);
                newView = (AutoCompleteTextView) activeView.findViewById(R.id.ListOfURLs);
                newView.setAdapter(mSuggestions);
                activeView.setTag(newView);
            } else {
                newView = (AutoCompleteTextView) activeView.getTag();
            }

            if (!newView.getText().toString().isEmpty())
            	newView.setText((mList.get(position).getText().toString()));
            newView.setId(position);
            
            newView.setOnFocusChangeListener(new OnFocusChangeListener() {
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (!hasFocus){
                            final int position = view.getId();
                            final AutoCompleteTextView urls = (AutoCompleteTextView) view;
                            mList.set(position, urls);
                        }
                    }
                }
            );
            
            return newView;
        }
    }
}
