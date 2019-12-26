package com.appsbygreatness.ideasappformobiledevelopers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ViewBitmaps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bitmaps);

        ImageView bitmapIV = findViewById(R.id.bitmapIV);
        ImageView bitmapBackButton = findViewById(R.id.bitmapBackButton);

        bitmapBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<Idea> ideas = Singleton.getInstance().getIdea();

        int ideaPosition = getIntent().getIntExtra("Idea position",0);
        int bitmapPosition = getIntent().getIntExtra("Bitmap position", 0);

        if (ideas.get(ideaPosition).getImageNames().size() >= 1) {

            File f = new File((ideas.get(ideaPosition).getFullpath()), (ideas.get(ideaPosition).getImageNames().get(bitmapPosition)));

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmapIV.setImageBitmap(bitmap);
        }

    }
}
