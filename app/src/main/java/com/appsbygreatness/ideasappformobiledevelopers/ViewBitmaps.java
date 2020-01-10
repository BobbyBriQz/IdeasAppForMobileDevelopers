package com.appsbygreatness.ideasappformobiledevelopers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.appsbygreatness.ideasappformobiledevelopers.database.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ViewBitmaps extends AppCompatActivity {

    ImageView bitmapIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bitmaps);

        bitmapIV = findViewById(R.id.bitmapIV);
        ImageView bitmapBackButton = findViewById(R.id.bitmapBackButton);

        bitmapBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final String imagePath = getIntent().getStringExtra("Image path");
        final String imageName = getIntent().getStringExtra("Image name");

        setImage(imagePath,imageName);

    }

    private void setImage(String imagePath, String imageName) {

            File f = new File(imagePath, imageName);

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmapIV.setImageBitmap(bitmap);

    }
}
