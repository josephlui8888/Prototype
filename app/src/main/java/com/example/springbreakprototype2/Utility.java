package com.example.springbreakprototype2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public abstract class Utility {

    // returns Bitmap image representation given encoded base64 string
    public static Bitmap decodeToImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT); // convert to byte array
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // convert to bitmap image
        return image;
    }
}
