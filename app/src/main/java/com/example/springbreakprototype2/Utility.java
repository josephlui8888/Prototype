package com.example.springbreakprototype2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Utility {

    // Converts bitmap to encoded base64 string
    // bitmaps are converted to byte arrays
    // byte arrays are converted to string
    public static String encodeToString(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        byte[] b = baos.toByteArray(); // byte array
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT); // string
        return encodedImage;
    }

    // returns Bitmap image representation given encoded base64 string
    public static Bitmap decodeToImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT); // convert to byte array
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // convert to bitmap image
        return image;
    }

    private static Bitmap lim (String url) {
        try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (IOException e) {
            Log.d("TEST", "Failed to load from URL: " + url);
            Log.d("TEST", "Failed to load from URL: " + e.getMessage());
            return null;
        }
    }

    public static Bitmap LoadImageFromWebOperations(final String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Bitmap> result = executor.submit(new Callable<Bitmap>() {
            public Bitmap call() throws Exception {
                return lim(url);
            }
        });

        try {
            Bitmap returnValue = result.get();
            return returnValue;
        } catch (Exception e) {
            Log.d("TEST", "Failed to load from URL: " + url);
            Log.d("TEST", "Failed to load from URL: " + e.getMessage());
            return null;
        }
    }

}
