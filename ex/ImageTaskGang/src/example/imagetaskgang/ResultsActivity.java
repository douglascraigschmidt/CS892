package example.imagetaskgang;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @class ResultsActivity
 *
 * @brief Shows the results of the ImageDownloadTask in
 * an easily understood and clear format
 */
public class ResultsActivity extends ListActivity {
    /**
     * The names of the filters used in the
     * ImageDownloadTask. This is used to organize the
     * results into groups
     */
    private String[] mFilterNames;

    /**
     * The layout that contains the buttons that 
     * are responsible for loading the images into the 
     * ListView
     */
    private LinearLayout mLayout;

    /**
     * The adapter responsible for loading the results into
     * the ListView
     */
    private ImageAdapter bitmapAdapter;
	
    /**
     * Creates the activity and generates a button for 
     * each filter applied to the images. These buttons
     * load change the bitmapAdapter's source to a new directory,
     * from which it will load images into the ListView.
     */
    @SuppressLint("InflateParams")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        // Sets the adapter to an ImageAdapter (defined below)
        bitmapAdapter = new ImageAdapter(this);
        setListAdapter(bitmapAdapter);
		
        setContentView(R.layout.activity_result);
		
        mLayout = (LinearLayout) findViewById(R.id.buttonList);
        
        // Retrieves the names of the filters applied to this
        // set of downloads.
        mFilterNames =
            getIntent().getStringArrayExtra(MainActivity.FILTER_EXTRA);

        // Iterate over the filter names and generate a button for 
        // each filter
        for (String filterName : mFilterNames) {
        	
        	// Create a new button with the layout of "result_button"
            Button resultButton = 
                (Button) LayoutInflater.from(this).inflate (R.layout.result_button,
                                                            null);
            
            // Set the new button's text and tag to the filter name
            resultButton.setText(filterName);
            resultButton.setTag(filterName);
            
            // When the button is clicked, change the bitmapAdapter
            // source to the appropriate filter directory
            resultButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button button =
                            (Button) view;
                        
                        // Find the filter directory and load the directory
                        // as the source of the bitmapAdapter
                        bitmapAdapter.setBitmaps
                            (new File(PlatformStrategy.instance().getDirectoryPath(),
                                      button.getText().toString()).getAbsolutePath());
					
                    }
                });
            
            // Add the button to the layout
            mLayout.addView(resultButton);
        }
    }

    /**
     * @class ImageAdapter
     *
     * @brief The Adapter that loads the images into the
     * Layout's ListView
     */
    public class ImageAdapter extends BaseAdapter {
        /**
         * The Context of the application
         */
        private Context mContext;

        /**
         * the ArrayList of bitmaps that hold the thumbnail images
         */
        private ArrayList<Bitmap> mBitmaps;

        /**
         * Creates the ImageAdapter in the given context
         */
        public ImageAdapter(Context c) {
            mContext = c;
            mBitmaps = new ArrayList<Bitmap>();
        }

        /**
         * Returns the count of bitmaps in the list
         */
        @Override
            public int getCount() {
            return mBitmaps.size();
        }

        /**
         * Returns the bitmap at the given position
         */
        @Override
            public Object getItem(int position) {
            return mBitmaps.get(position);
        }

        /**
         * Returns the given position as the Id of the bitmap.
         * This works because the bitmaps are stored in a
         * sequential manner.
         */
        @Override
            public long getItemId(int position) {
            return position;
        }

        /**
         * Returns the view. This method is necessary for
         * filling the ListView appropriately
         */
        @Override
        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(mBitmaps.get(position));
            return imageView;
        }

        /**
         * Resets the bitmaps of the ListView to the one's
         * found at the given filterPath
         */
        private void setBitmaps(String filterPath) {
            File[] bitmaps = new File(filterPath).listFiles();
            mBitmaps = new ArrayList<Bitmap>();

            for (File bitmap : bitmaps){
                if (bitmap != null) {
                	// @@ Nolan Gridview?
                    mBitmaps.add
                        (BitmapFactory.decodeFile(bitmap.getAbsolutePath()));
                }
            }
            notifyDataSetChanged();
        }
    }

}
