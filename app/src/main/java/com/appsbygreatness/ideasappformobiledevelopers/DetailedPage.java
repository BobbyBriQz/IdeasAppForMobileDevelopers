package com.appsbygreatness.ideasappformobiledevelopers;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.appsbygreatness.ideasappformobiledevelopers.adapters.BitmapAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.database.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailedPage extends AppCompatActivity implements BitmapAdapter.OnBitmapClickListener, BitmapAdapter.OnLongBitmapClickListener{

    EditText detailedAppName, detailedAppIdea, detailedFunctionality, detailedTodo;
    int id;
    Idea idea;
    RecyclerView detailedRV;
    BitmapAdapter adapter;
    Bitmap bitmap;
    String imageName, fullPath;
    IdeaRepository ideaRepository;

    public static final int DETAILED_PAGE_RESULTCODE = 12;
    public static final int IMPORT_IMAGE_REQUESTCODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_page);

        Toolbar detailedAppbar = findViewById(R.id.detailedAppbar);
        setSupportActionBar(detailedAppbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Edit your app ideas");
        detailedAppbar.setNavigationIcon(R.drawable.ic_arrow_back);
        detailedAppbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                killActivity();
            }
        });

        detailedRV = findViewById(R.id.detailedRV);

        Intent intent = getIntent();

        id = intent.getIntExtra("id", 0);


        detailedAppName = findViewById(R.id.detailedAppName);
        detailedAppIdea = findViewById(R.id.detailedAppIdea);
        detailedFunctionality = findViewById(R.id.detailedFunctionality);
        detailedTodo = findViewById(R.id.detailedTodo);
        ideaRepository = new IdeaRepository(this);



        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
               idea =  ideaRepository.getIdea(id);
                setUpRecyclerView();

                AppExecutors.getInstance().getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });

            }
        });

    }

    private void killActivity() {
        Intent intent = new Intent(getApplicationContext(), ViewIdeas.class);
        startActivity(intent);
        finish();
    }

    private void updateUI() {

        detailedAppName.setText(idea.getName());
        detailedAppIdea.setText(idea.getIdea());
        detailedFunctionality.setText(idea.getFunctionality());
        detailedTodo.setText(idea.getTodo());

    }

    private void setUpRecyclerView() {

        adapter = new BitmapAdapter(idea.getFullpath(), idea.getImageNames(), this, this, this);

        detailedRV.setAdapter(adapter);
        detailedRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == IMPORT_IMAGE_REQUESTCODE && data!= null){

            Uri selectedImage = data.getData();
            Log.i("DetailedPage", "gotten Uri, about to load bitmap");

            try {



                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                SaveImageToInternalStorage saveObject = new SaveImageToInternalStorage();
                fullPath = saveObject.execute(bitmap).get();


                idea.setFullPath(fullPath);

                adapter.notifyDataSetChanged();



            } catch (Exception e) {
                e.printStackTrace();

                Log.i("DetailedPage", "onActivityResult: failed ");
                Log.i("DetailedPage", e.toString());
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public class SaveImageToInternalStorage extends AsyncTask<Bitmap, Void, String> {


        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];

            Date date = Calendar.getInstance().getTime();
            imageName = new SimpleDateFormat("dd-MM-YYYY, HH:mm:ss a", Locale.getDefault()).format(date) + "jpg";

            idea.addImageName(imageName);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            File directory = cw.getDir("Images", Context.MODE_PRIVATE);

            File mypath = new File(directory, (imageName));

            FileOutputStream fileOutputStream;
            Log.i("DetailedPage", "About to commence save");

            try {
                fileOutputStream = new FileOutputStream(mypath);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                fileOutputStream.close();
                Log.i("DetailedPage", "Save: Successful");
                //loadBitmapToImageView(directory.getAbsolutePath());

                return directory.getAbsolutePath();


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("DetailedPage", "Save to internal storage: failed ");
            }


            return null;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_idea_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.saveIdea){


            saveIdea();


        }if (item.getItemId() == R.id.importImage){

            importImage();
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveIdea(){

        if (detailedAppName.getText().toString().equals("")){

            Toast.makeText(getApplicationContext(), "Please input an app name", Toast.LENGTH_LONG).show();

            return;
        }

        Date date = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("dd-MM-YYYY, HH:mm:ss a", Locale.getDefault()).format(date);




        idea.setName(detailedAppName.getText().toString());
        idea.setIdea(detailedAppIdea.getText().toString());
        idea.setFunctionality(detailedFunctionality.getText().toString());
        idea.setTodo(detailedTodo.getText().toString());
        idea.setTimestamp(timeStamp);

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                ideaRepository.updateIdea(idea);
                killActivity();
            }
        });


    }

    public void importImage(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMPORT_IMAGE_REQUESTCODE );
            }else{

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMPORT_IMAGE_REQUESTCODE);

            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == IMPORT_IMAGE_REQUESTCODE && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMPORT_IMAGE_REQUESTCODE);
                }
            }

    }

    @Override
    public void onBitmapClick(int bitmapPosition) {

        Intent intent = new Intent(getApplicationContext(), ViewBitmaps.class);

        intent.putExtra("Image path", idea.getFullPath());
        intent.putExtra("Image name", idea.getImageNames().get(bitmapPosition));

        startActivity(intent);
    }

    @Override
    public void onLongBitmapClick(final int bitmapPosition) {

        final String imageNameToBeDeleted = idea.getImageNames().get(bitmapPosition);

        new AlertDialog.Builder(this)
                .setTitle("Delete App Idea")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete picture?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        idea.getImageNames().remove(bitmapPosition);

                        if(deleteImage(idea.getFullPath(), imageNameToBeDeleted)){
                            Toast.makeText(getApplicationContext(),
                                    "Picture deleted",
                                    Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();



                    }
                }).show();

    }


    public boolean deleteImage(String imagePath, String imageName){

        File f = new File(imagePath, imageName);

        boolean imageIsDeleted = false;
        try {
            imageIsDeleted = f.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageIsDeleted;
    }
}
