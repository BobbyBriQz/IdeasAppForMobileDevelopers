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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewIdea extends AppCompatActivity implements BitmapAdapter.OnBitmapClickListener, BitmapAdapter.OnLongBitmapClickListener {



    EditText newAppName, newAppIdea, newFunctionality, newTodo;

    ArrayList<Idea> ideas;
    Bitmap bitmap;
    //ArrayList<Bitmap> bitmaps;
    BitmapAdapter adapter;

    String imageName, fullPath;

    ArrayList<String> fullPaths;
    ArrayList<String> imageNames;

    public static final int NEW_IDEAS_RESULTCODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        Toolbar newAppbar = findViewById(R.id.newAppbar);
        setSupportActionBar(newAppbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Register new app idea");
        newAppbar.setNavigationIcon(R.drawable.ic_arrow_back);
        newAppbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        newAppName = findViewById(R.id.newAppName);
        newAppIdea = findViewById(R.id.newAppIdea);
        newFunctionality = findViewById(R.id.newFunctionality);
        newTodo = findViewById(R.id.newTodo);

        RecyclerView newRV = findViewById(R.id.newRV);

        ideas = Singleton.getInstance().getIdea();

        //bitmaps = new ArrayList<>();
        fullPaths = new ArrayList<>();
        imageNames = new ArrayList<>();

        adapter = new BitmapAdapter(fullPaths, imageNames,this, this, this);

        newRV.setAdapter(adapter);
        newRV.setLayoutManager(new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate( R.menu.new_idea_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.saveIdea) {

            saveNewIdea();


        }else if (item.getItemId() == R.id.importImage){

            importImage();

        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNewIdea() {

        if (newAppName.getText().toString().equals("")){

            Toast.makeText(getApplicationContext(), "Please input an app name", Toast.LENGTH_LONG).show();

            return;
        }

        String appName = newAppName.getText().toString();
        String appIdea = newAppIdea.getText().toString();
        String functionality = newFunctionality.getText().toString();
        String todo = newTodo.getText().toString();

        Date date = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("dd-MM-YYYY, HH:mm:ss a", Locale.getDefault()).format(date);

        Intent saveNewIdea = new Intent(getApplicationContext(), ViewIdeas.class);
        saveNewIdea.putExtra("position", 0);

        ideas.add(new Idea(appName, appIdea, functionality, todo, timeStamp, fullPaths, imageNames));

        setResult(NEW_IDEAS_RESULTCODE, saveNewIdea);
        finish();

    }

    public void importImage(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DetailedPage.IMPORT_IMAGE_REQUESTCODE );
            }else{

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, DetailedPage.IMPORT_IMAGE_REQUESTCODE);

            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DetailedPage.IMPORT_IMAGE_REQUESTCODE && data != null){

            Uri selectedImage = data.getData();

            try {



                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

               //bitmaps.add(bitmap);
                SaveToInternalStorage saveObject = new SaveToInternalStorage();
                fullPath = saveObject.execute(bitmap).get();

                fullPaths.add(fullPath);

                adapter.notifyDataSetChanged();




            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onBitmapClick(int position) {

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
                        imageNames.remove(bitmapPosition);
                        fullPaths.remove(bitmapPosition);

                        adapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(),
                                "Picture deleted",
                                Toast.LENGTH_SHORT).show();

                    }
                }).show();

    }

    public class SaveToInternalStorage extends AsyncTask<Bitmap, Void, String> {


        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];

            Date date = Calendar.getInstance().getTime();
            imageName = new SimpleDateFormat("dd-MM-YYYY, HH:mm:ss a", Locale.getDefault()).format(date) + "jpg";

            imageNames.add(imageName);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            File directory = cw.getDir("Images", Context.MODE_PRIVATE);

            File mypath = new File(directory, (imageName));

            FileOutputStream fileOutputStream = null;
            Log.i("NewIdea", "About to commence save");

            try {
                fileOutputStream = new FileOutputStream(mypath);


                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);


                fileOutputStream.close();
                Log.i("NewIdea", "Save: Successful");
                //loadBitmapToImageView(directory.getAbsolutePath());

                return directory.getAbsolutePath();


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("NewIdea", "Save to internal storage: failed ");
            }


            return null;
        }


    }
}
