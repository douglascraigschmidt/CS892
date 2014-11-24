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
 * @brief @@ Nolan, please fill in here.
 */
public class ResultsActivity extends ListActivity {
    /**
     * @@ Nolan, please fill in here.
     */
    private String[] mFilterNames;

    /**
     * @@ Nolan, please fill in here.
     */
    private LinearLayout mLayout;

    /**
     * @@ Nolan, please fill in here.
     */
    private ImageAdapter bitmapAdapter;
	
    /**
     * @@ Nolan, please fill in here.
     */
    @SuppressLint("InflateParams")
    @Override protected void onCreate(Bundle savedInstanceState) {
        // @@ Nolan, please document the logic in this method.
        super.onCreate(savedInstanceState);
		
        bitmapAdapter = new ImageAdapter(this);
        setListAdapter(bitmapAdapter);
		
        setContentView(R.layout.activity_result);
		
        mLayout = (LinearLayout) findViewById(R.id.buttonList);
        mFilterNames =
            getIntent().getStringArrayExtra(MainActivity.FILTER_EXTRA);

        for (String filterName : mFilterNames) {
            Button resultButton = 
                (Button) LayoutInflater.from(this).inflate (R.layout.result_button,
                                                            null);
            resultButton.setText(filterName);
            resultButton.setTag(filterName);
            resultButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button button =
                            (Button) view;
                        bitmapAdapter.setBitmaps
                            (new File(PlatformStrategy.instance().getDirectoryPath(),
                                      button.getText().toString()).getAbsolutePath());
					
                    }
                });
            mLayout.addView(resultButton);
        }
    }

    /**
     * @class ImageAdapter
     *
     * @brief @@ Nolan, please fill in here.
     */
    public class ImageAdapter extends BaseAdapter {
        /**
         * @@ Nolan, please fill in here.
         */
        private Context mContext;

        /**
         * @@ Nolan, please fill in here.
         */
        private ArrayList<Bitmap> mBitmaps;

        /**
         * @@ Nolan, please fill in here.
         */
        public ImageAdapter(Context c) {
            mContext = c;
            mBitmaps = new ArrayList<Bitmap>();
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
            public int getCount() {
            return mBitmaps.size();
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
            public Object getItem(int position) {
            return mBitmaps.get(position);
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
            public long getItemId(int position) {
            return position;
        }

        /**
         * @@ Nolan, please fill in here.
         */
        @Override
        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(mBitmaps.get(position));
            return imageView;
        }

        /**
         * @@ Nolan, please fill in here.
         */
        private void setBitmaps(String filterPath) {
            File[] bitmaps = new File(filterPath).listFiles();
            mBitmaps = new ArrayList<Bitmap>();

            for (File bitmap : bitmaps){
                if (bitmap != null) {
                    File bitmapFile =
                        new File(bitmap.toString());
                    mBitmaps.add
                        (BitmapFactory.decodeFile
                         (bitmapFile.getAbsolutePath()));
                }
            }
            notifyDataSetChanged();
        }
    }

}
