package com.example.springbreakprototype2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface onItemClickListener {
        void onItemClick(Product p);
    }
    private Product[] data;
    private final onItemClickListener listener;

    public ProductAdapter(Product[] data, onItemClickListener listener) {

        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(NumberFormat.getCurrencyInstance().format(data[position].getPrice()));
        String s = data[position].getTitle();
        if (s.length() >= 30) {
            s = s.substring(0, 27) + "...";
        }
        holder.textView2.setText(s);
        s = data[position].getDescription();
        if (s.length() >= 36) {
            s = s.substring(0, 34) + "...";
        }
        holder.textView3.setText(s);

        // convert image from base64 String to image
        String encodedImage = data[position].getThumbnailImage();
        Bitmap image = decodeToImage(encodedImage);
        holder.imageView.setImageBitmap(image);
        holder.bind(data[position], listener);
    }

    // returns Bitmap image representation given encoded base64 string
    public Bitmap decodeToImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT); // convert to byte array
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // convert to bitmap image
        return image;
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView2;
        public TextView textView3;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.textView2 = (TextView) itemView.findViewById(R.id.textView2);
            this.textView3 = (TextView) itemView.findViewById(R.id.textView3);
        }

        private void bind(final Product item, final onItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }
}
