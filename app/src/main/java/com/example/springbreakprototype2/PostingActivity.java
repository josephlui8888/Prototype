package com.example.springbreakprototype2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.springbreakprototype2.Utility;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class PostingActivity extends AppCompatActivity {
    private TextView good_service;
    private Spinner categories;
    private TextInputLayout title_text_field, description_text_field, price_text_field;
    private String type, title_value, description_value, category_value, user_name;
    private Double price_value;
    private FirebaseFirestore db;
    private StorageReference storage;
    private String[] list_categories;
    private String[] list_categories_good = {"Furniture", "Textbooks", "Clothes", "Misc."};
    private String[] list_categories_service = {"Tutoring", "Moving", "Haircuts", "Misc."};
    private int imageNum;
    private static final int RESULT_LOAD_IMAGES = 1;
    Bundle extras;

    private HashMap<Integer, Uri> postingImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        good_service = findViewById(R.id.good_service);
        title_text_field = findViewById(R.id.title);
        description_text_field = findViewById(R.id.description);
        price_text_field = findViewById(R.id.price);
        categories = findViewById(R.id.categories);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference("app_images/");

        Intent intent = getIntent();
        extras = intent.getExtras();
        type = extras.getString("GOOD_SERVICE");
        user_name = extras.getString("USERNAME");
        if (type.equals("good")) {
            good_service.setText("New Posting for Goods");
        } else
            good_service.setText("New Posting for Services");

        if (type.equals("service")) {
            list_categories = list_categories_service;
        } else {
            list_categories = list_categories_good;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);

        this.postingImages = new HashMap<>();
        imageNum = 0;
    }

    //Publish button, go back to home activity page
    public void publish(View view) {
        title_value = title_text_field.getEditText().getText().toString();
        description_value = description_text_field.getEditText().getText().toString();
        String s = price_text_field.getEditText().getText().toString();
        category_value = categories.getSelectedItem().toString();

        if (title_value.equals("") || description_value.equals("") || category_value.equals("") || s.equals("")) {
            Toast.makeText(getApplicationContext(), "Must fill out all fields", Toast.LENGTH_LONG).show();
        } else {
            price_value = Double.parseDouble(s);

            try {
                addToDatabase(title_value, description_value, category_value, user_name, price_value);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to add to database", Toast.LENGTH_LONG).show();
            }
//            Toast.makeText(getApplicationContext(), "Title: " + title_value + " , Description: " +
//                            description_value + " , Category: " + category_value + " , Price: " + price_value
//                    , Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, HomeActivity.class);

            extras.putString("PREVIOUS", "posting");
            intent.putExtras(extras);

            startActivity(intent);
        }
    }

    public void uploadImages(View view) {
        // check for gallery access permissions
        if(ActivityCompat.checkSelfPermission(PostingActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostingActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return;
        }

        // access gallery
        Intent uploadIntent = new Intent(Intent.ACTION_GET_CONTENT);
        uploadIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        uploadIntent.setType("image/*");
        startActivityForResult(uploadIntent, RESULT_LOAD_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RESULT_LOAD_IMAGES && resultCode == RESULT_OK && data != null) {

            List<Uri> imageUris = new ArrayList<>();
            ClipData clipData = data.getClipData();

            // clipData is null if only one picture selected
            if(clipData != null) {
                for(int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else {
                imageUris.add(data.getData());
            }

            if(postingImages.size() == 0) {
                // make original "add pictures" button invisible
                Button uploadImages = findViewById(R.id.uploadImages);
                uploadImages.setVisibility(View.GONE);
            }

            // displaying all selected pictures in the linear layout (with horizontal scroll)
            LinearLayout layout = findViewById(R.id.imagesLinear);
            for (int i = 0; i < imageUris.size(); i++) {
                // create relative layout for image and remove image button
                final RelativeLayout imageLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 20, 0);
                imageLayout.setLayoutParams(params);
                imageLayout.setId(imageNum);

                // create imageview to hold uploaded image
                final ImageView imageView = new ImageView(this);
                imageView.setId(imageNum);
                imageView.setPadding(0, 20, 20, 2);
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 550);
                imageView.setLayoutParams(imageParams);
                imageView.setImageURI(imageUris.get(i));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                imageLayout.addView(imageView);

                // create button to remove image
                final ImageButton removeButton = new ImageButton(this);
                removeButton.setImageResource(R.drawable.round_clear_24);
                removeButton.setAdjustViewBounds(true);
                removeButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                removeButton.setPadding(4, 4, 4, 4);
                removeButton.setBackground(getDrawable(R.drawable.round_corner));
                removeButton.setId(imageNum);
                RelativeLayout.LayoutParams removeButtonParams = new RelativeLayout.LayoutParams(
                        65, 65);
                removeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
                removeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                removeButton.setLayoutParams(removeButtonParams);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewManager)(imageLayout.getParent())).removeView(imageLayout);
                        postingImages.remove(removeButton.getId());
                        if(postingImages.size() == 0){
                            Button smallUploadImages = findViewById(R.id.smallUploadImages);
                            smallUploadImages.setVisibility(View.GONE);
                            HorizontalScrollView images = findViewById(R.id.horizontalScrollImages);
                            images.setVisibility(View.GONE);
                            Button uploadImages = findViewById(R.id.uploadImages);
                            uploadImages.setVisibility(View.VISIBLE);
                        }
                    }
                });

                imageLayout.addView(removeButton);

                // add image to list of images to post to database
                this.postingImages.put(imageNum, imageUris.get(i));
                //Log.d("TEST", imageUris.get(i).toString());

                layout.addView(imageLayout);
                imageNum++;
            }

            // make smaller upload images button visible at end of pictures layout
            Button smallUploadImages = findViewById(R.id.smallUploadImages);
            ((ViewManager)(smallUploadImages.getParent())).removeView(smallUploadImages);
            smallUploadImages.setVisibility(View.VISIBLE);
            layout.addView(smallUploadImages);

            // make scrolling images view visible
            HorizontalScrollView images = findViewById(R.id.horizontalScrollImages);
            images.setVisibility(View.VISIBLE);
        }
    }

    private void addToDatabase (final String title, final String description, final String category, final String seller, final Double price) throws IOException {
        int NUM_IMAGES_MAX = 3;
        final String [] pi = new String [NUM_IMAGES_MAX];
        Set<Integer> keys = this.postingImages.keySet();
        Iterator<Integer> keyIter = keys.iterator();

        // Converts the uploaded image uri to bitmaps
        // bitmaps are converted to byte arrays
        // byte arrays are converted to string
        for (int i = 0; i < NUM_IMAGES_MAX; i++) { // only allow 3 images for now
            if (i + 1 > this.postingImages.size()) {
                // use an empty string if an image isn't provided
                pi[i] = "";
            } else {
                Uri current = this.postingImages.get(keyIter.next());
                //Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), current); // uri to bitmap
                //pi[i] = Utility.encodeToString(bm); // bm to encoded base64 string

                // upload to storage
                String filename = seller.replaceAll("\\W+","") + "_" + title.replaceAll("\\W+","") + "_" + i + "_" + System.currentTimeMillis();
                final StorageReference sr = this.storage.child(filename);
                final int finalI = i;
                sr.putFile(current)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Get a URL to the uploaded content
                                        pi[finalI] = uri.toString();
                                        //Toast.makeText(getApplicationContext(), "Successful upload to storage: " + pi[finalI] , Toast.LENGTH_LONG).show();
                                        finish();
                                        return;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        finish();
                                        return;
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "Failed to upload image to storage", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                actualUpload( title,  description,  category,  seller,  price,  pi[0],  pi[1],  pi[2]);
            }
        }, 5000);

    }

    // actually pushes data to database
    private void actualUpload(String title, String description, String category, String seller, Double price, String one, String two, String three) {
        Product p = new Product(price, seller, title, description, FieldValue.serverTimestamp(), category, one, two, three);
        if (type.toLowerCase().equals("good")) {
            db.collection("products").document(type.toLowerCase()).collection(category.toLowerCase())
                    .document("temp").collection("good_price").add(p);
        } else {
            db.collection("products").document(type.toLowerCase()).collection(category.toLowerCase())
                    .document("temp").collection("service_price").add(p);
        }
    }

}
