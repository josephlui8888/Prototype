package com.example.springbreakprototype2;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.springbreakprototype2.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<String> mImageIds;
    private StorageReference storage;

    ImageAdapter(Context context, ArrayList<String> imageStrings) {
        mContext = context;
        mImageIds = imageStrings;
        this.storage = FirebaseStorage.getInstance().getReference("app_images/");
    }

    @Override
    public int getCount() {
        return mImageIds.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private Bitmap getBitmapFromUrl(String url) throws IOException {
        return Utility.LoadImageFromWebOperations(url);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        try {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            String url = mImageIds.get(position);

            Log.d("TEST", "Instantiating bitmap from url");
            Bitmap imageBitmap = getBitmapFromUrl(url); //Utility.decodeToImage(imageString);
            imageView.setImageBitmap(imageBitmap);
            container.addView(imageView, 0);
            return imageView;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
