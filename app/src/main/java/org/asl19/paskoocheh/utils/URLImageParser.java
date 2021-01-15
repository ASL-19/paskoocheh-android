package org.asl19.paskoocheh.utils;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import org.asl19.paskoocheh.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class URLImageParser implements Html.ImageGetter {
    Context context;
    TextView container;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     * @param textView
     * @param context
     */
    public URLImageParser(TextView textView, Context context) {
        this.context = context;
        this.container = textView;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask =
            new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable drawable) {
            this.urlDrawable = drawable;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                final float scalingFactor = (float) container.getMeasuredWidth() / result.getIntrinsicWidth();
                final int height = (int) (result.getIntrinsicHeight() * scalingFactor);

                urlDrawable.setBounds(0, 0, container.getMeasuredWidth(), height);
                urlDrawable.drawable = result;
                URLImageParser.this.container.invalidate();
                URLImageParser.this.container.setHeight((URLImageParser.this.container.getMeasuredHeight() + height));
                URLImageParser.this.container.setEllipsize(null);
                URLImageParser.this.container.setText(container.getText());
            } else {
                Toast.makeText(context, context.getString(R.string.image_load_fail_due_to_memory), Toast.LENGTH_SHORT).show();
            }
        }

        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeStream(is, null, options);
                final int bitmapHeight = options.outHeight;
                final int bitmapWidth = options.outWidth;
                is.close();

                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);

                final int numBytesPerPixel = 4;
                final int minimumMemoryThreshold = 1000000;
                int minimumAllocationNeeded = bitmapHeight * bitmapWidth * numBytesPerPixel;
                if (memoryInfo.availMem - minimumAllocationNeeded < minimumMemoryThreshold) {
                    return null;
                }

                is = fetch(urlString);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                is.close();
                Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                final float scalingFactor = (float) container.getMeasuredWidth() / drawable.getIntrinsicWidth();
                drawable.setBounds(0, 0, container.getMeasuredWidth(), (int) (drawable.getIntrinsicHeight() * scalingFactor));
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream stream = urlConnection.getInputStream();
            return stream;
        }
    }
}
