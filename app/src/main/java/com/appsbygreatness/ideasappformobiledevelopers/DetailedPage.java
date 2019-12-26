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
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailedPage extends AppCompatActivity implements BitmapAdapter.OnBitmapClickListener, BitmapAdapter.OnLongBitmapClickListener{

    EditText detailedAppName, detailedAppIdea, detailedFunctionality, detailedTodo;
    int position;
    ArrayList<Idea> ideas;
    BitmapAdapter adapter;
    Bitmap bitmap;
    String imageName, fullPath;

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

                finish();
            }
        });

        RecyclerView detailedRV = findViewById(R.id.detailedRV);

        Intent intent = getIntent();

        position = intent.getIntExtra("Position", 0);


        detailedAppName = findViewById(R.id.detailedAppName);
        detailedAppIdea = findViewById(R.id.detailedAppIdea);
        detailedFunctionality = findViewById(R.id.detailedFunctionality);
        detailedTodo = findViewById(R.id.detailedTodo);


        ideas = Singleton.getInstance().getIdea();

        Log.i("DetailedPage", "Singleton: successfull ");

        adapter = new BitmapAdapter(ideas.get(position).getFullpath(), ideas.get(position).getImageNames(), this, this, this);

        detailedRV.setAdapter(adapter);
        detailedRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) );




        detailedAppName.setText(ideas.get(position).getName());
        detailedAppIdea.setText(ideas.get(position).getIdea());
        detailedFunctionality.setText(ideas.get(position).getFunctionality());
        detailedTodo.setText(ideas.get(position).getTodo());

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


                ideas.get(position).setFullPath(fullPath);

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

            ideas.get(position).addImageName(imageName);

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


        Intent intentSave = new Intent(DetailedPage.this, ViewIdeas.class);

        ideas.get(position).setName(detailedAppName.getText().toString());
        ideas.get(position).setIdea(detailedAppIdea.getText().toString());
        ideas.get(position).setFunctionality(detailedFunctionality.getText().toString());
        ideas.get(position).setTodo(detailedTodo.getText().toString());
        ideas.get(position).setTimestamp(timeStamp);

        setResult(DETAILED_PAGE_RESULTCODE, intentSave);

        finish();
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

        intent.putExtra("Idea position", position);
        intent.putExtra("Bitmap position", bitmapPosition);

        startActivity(intent);
    }

    @Override
    public void onLongBitmapClick(final int bitmapPosition) {

        new AlertDialog.Builder(this)
                .setTitle("Delete App Idea")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete picture?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ideas.get(position).getImageNames().remove(bitmapPosition);


                        adapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(),
                                "Picture deleted",
                                Toast.LENGTH_SHORT).show();

                    }
                }).show();

    }
}
