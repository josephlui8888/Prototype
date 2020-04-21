package com.example.springbreakprototype2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public abstract class Utility {

    // Converts bitmap to encoded base64 string
    // bitmaps are converted to byte arrays
    // byte arrays are converted to string
    public static String encodeToString(Bitmap bm){
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
}
