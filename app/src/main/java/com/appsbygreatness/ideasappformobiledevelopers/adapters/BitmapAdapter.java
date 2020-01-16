package com.appsbygreatness.ideasappformobiledevelopers.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsbygreatness.ideasappformobiledevelopers.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class BitmapAdapter extends RecyclerView.Adapter<BitmapAdapter.ViewHolder> {

    //private ArrayList<Bitmap> bitmaps;
    private String fullPath;
    private List<String> imageNames;


    Context context;
    OnBitmapClickListener onBitmapClickListener;
    OnLongBitmapClickListener onLongBitmapClickListener;

    public BitmapAdapter(String fullPath, List<String> imageNames, Context context, OnBitmapClickListener onBitmapClickListener, OnLongBitmapClickListener onLongBitmapClickListener) {
        //this.bitmaps = bitmaps;

        /*this.fullPath = new ArrayList<>();
        this.imageNames = new ArrayList<>();*/


        this.fullPath = fullPath;


        if(imageNames != null){

            this.imageNames = imageNames;
        }
        this.context = context;
        this.onBitmapClickListener = onBitmapClickListener;
        this.onLongBitmapClickListener = onLongBitmapClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_image_rv, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view, onBitmapClickListener, onLongBitmapClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (imageNames.size() >= 1) {

            File f = new File(fullPath, (imageNames.get(i)));

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            viewHolder.image.setImageBitmap(bitmap);
        }



    }

    @Override
    public int getItemCount() {
        return imageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView image;
        OnBitmapClickListener onBitmapClickListener;
        OnLongBitmapClickListener onLongBitmapClickListener;


        public ViewHolder(@NonNull View itemView, OnBitmapClickListener onBitmapClickListener, OnLongBitmapClickListener onLongBitmapClickListener) {
            super(itemView);

            image = itemView.findViewById(R.id.imageBitmapRV);
            this.onBitmapClickListener = onBitmapClickListener;
            this.onLongBitmapClickListener = onLongBitmapClickListener;

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onBitmapClickListener.onBitmapClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {

            onLongBitmapClickListener.onLongBitmapClick(getAdapterPosition());

            return true;
        }
    }

    public interface OnBitmapClickListener{

        void onBitmapClick(int position);
    }

    public interface OnLongBitmapClickListener{

        void onLongBitmapClick(int position);
    }
}
