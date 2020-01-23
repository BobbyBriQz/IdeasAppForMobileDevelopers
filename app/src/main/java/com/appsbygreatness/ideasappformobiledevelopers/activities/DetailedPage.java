package com.appsbygreatness.ideasappformobiledevelopers.activities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.appsbygreatness.ideasappformobiledevelopers.R;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.BitmapAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.TodoAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.model.Todo;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailedPage extends AppCompatActivity implements BitmapAdapter.OnBitmapClickListener, BitmapAdapter.OnLongBitmapClickListener,
        TodoAdapter.OnTodoCompleteClickListener, TodoAdapter.OnTodoDeleteClickListener {

    EditText detailedAppName, detailedAppIdea, detailedFunctionality, addTodoET;
    int id;
    ImageButton addTodoButton;
    KonfettiView viewKonfetti;
    Idea idea;
    FloatingActionButton importImageFAB;
    RecyclerView detailedRV, todoRV;
    BitmapAdapter adapter;
    TodoAdapter todoAdapter;
    Bitmap bitmap;
    String imageName, fullPath;
    IdeaRepository ideaRepository;

    public static final int IMPORT_IMAGE_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme);
        }
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
        todoRV = findViewById(R.id.todoRV);

        Intent intent = getIntent();

        id = intent.getIntExtra("id", 0);


        addTodoButton = findViewById(R.id.addTodoButton);
        addTodoET = findViewById(R.id.addTodoET);
        detailedAppName = findViewById(R.id.detailedAppName);
        detailedAppIdea = findViewById(R.id.detailedAppIdea);
        detailedFunctionality = findViewById(R.id.detailedFunctionality);
        importImageFAB = findViewById(R.id.importImageFAB);
        viewKonfetti = findViewById(R.id.viewKonfetti);

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

        importImageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importImage();
            }
        });

        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todo = addTodoET.getText().toString();

                if(todo.trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please input task to do", Toast.LENGTH_SHORT)
                            .show();

                    return;
                }

                idea.getTodo().add(new Todo(todo, false));
                addTodoET.setText("");
                todoAdapter.notifyDataSetChanged();
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


    }

    private void setUpRecyclerView() {

        adapter = new BitmapAdapter(idea.getFullpath(), idea.getImageNames(), this, this, this);

        detailedRV.setAdapter(adapter);
        detailedRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) );

        todoAdapter = new TodoAdapter(idea.getTodo(), this, this, this);

        todoRV.setAdapter(todoAdapter);
        todoRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == IMPORT_IMAGE_REQUEST_CODE && data!= null){

            Uri selectedImage = data.getData();
            Log.i("DetailedPage", "gotten Uri, about to load bitmap");

            try {

                //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                bitmap = scaleImageFromUri(this, selectedImage);
//              Handle compression of bitmap here

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
            imageName = new SimpleDateFormat("dd-MM-YYYY,HH:mm:ssa", Locale.getDefault()).format(date) + ".jpg";

            idea.addImageName(imageName);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            //Get the directory path for Images in private mode (Inside app data)
            File directory = cw.getDir("Images", Context.MODE_PRIVATE);


            //Get the file path to which the bitmap is to be saved
            File mypath = new File(directory, (imageName));




            //FileOutStream to be used for the parsing of the bitmap to the myPath location
            FileOutputStream fileOutputStream;
            Log.i("DetailedPage", "About to commence save");

            try {
                fileOutputStream = new FileOutputStream(mypath);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                fileOutputStream.close();
                Log.i("DetailedPage", "Save: Successful");

                //This is where that saveBitmapToFile() should be called.

                //resizeBitmapInFile(mypath);

                return directory.getAbsolutePath();


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("DetailedPage", "Save to internal storage: failed ");
            }

            return null;
        }

    }

    public Bitmap scaleImageFromUri(Context context, Uri photoUri) throws IOException {

        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        dbo.inSampleSize = 6;
        BitmapFactory.decodeStream(is, null, dbo);
        assert is != null;
        is.close();

        int orientation = getOrientation(context, photoUri);


        // The new size we want to scale to
        final int REQUIRED_SIZE = 160;

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while (dbo.outWidth / scale / 2 >= REQUIRED_SIZE &&
                dbo.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
        }



        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        srcBitmap = BitmapFactory.decodeStream(is, null, options);
        assert is != null;
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            assert srcBitmap != null;
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }


        return srcBitmap;
    }


    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        assert cursor != null;
        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        return orientation;
    }


    /*public void resizeBitmapInFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            //file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);


        } catch (Exception e) {

        }
    }*/
    @Override
    public void onCompleteClick(int position) {
        boolean stateBeforeClick = idea.getTodo().get(position).isCompleted();
        boolean stateAfterClick = !stateBeforeClick;
        idea.getTodo().get(position).setCompleted(stateAfterClick);

        todoAdapter.notifyDataSetChanged();

        if(stateAfterClick){
            viewKonfetti.build()
                    .addColors(Color.YELLOW,Color.GREEN, Color.MAGENTA, Color.BLUE)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f,5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(new Size(12, 5))
                    .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                    .streamFor(300,5000L);
        }
    }

    @Override
    public void onDeleteClick(int position) {
        idea.getTodo().remove(position);
        todoAdapter.notifyDataSetChanged();

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


        }

        return super.onOptionsItemSelected(item);
    }

    public void saveIdea(){

        if (detailedAppName.getText().toString().equals("")){

            Toast.makeText(getApplicationContext(), "Please input an app name", Toast.LENGTH_LONG).show();

            return;
        }

        Date date = Calendar.getInstance().getTime();


        String timeStamp = new SimpleDateFormat("YYYY-MM-dd, HH:mm:ss a", Locale.getDefault()).format(date);




        idea.setName(detailedAppName.getText().toString());
        idea.setIdea(detailedAppIdea.getText().toString());
        idea.setFunctionality(detailedFunctionality.getText().toString());
        idea.setTimestamp(timeStamp);

        ideaRepository.updateIdea(idea);
        killActivity();



    }

    public void importImage(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMPORT_IMAGE_REQUEST_CODE);
            }else{

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMPORT_IMAGE_REQUEST_CODE);

            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == IMPORT_IMAGE_REQUEST_CODE && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMPORT_IMAGE_REQUEST_CODE);
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

    @Override
    public void onBackPressed() {
        killActivity();
    }
}
