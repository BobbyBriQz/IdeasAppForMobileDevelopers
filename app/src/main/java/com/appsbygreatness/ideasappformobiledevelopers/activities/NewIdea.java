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
import android.graphics.Matrix;
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

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.appsbygreatness.ideasappformobiledevelopers.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.R;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.BitmapAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.TodoAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.model.Todo;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.appsbygreatness.ideasappformobiledevelopers.activities.DetailedPage.IMPORT_IMAGE_REQUESTCODE;

public class NewIdea extends AppCompatActivity implements BitmapAdapter.OnBitmapClickListener, BitmapAdapter.OnLongBitmapClickListener,
        TodoAdapter.OnTodoDeleteClickListener, TodoAdapter.OnTodoCompleteClickListener {



    EditText newAppName, newAppIdea, newFunctionality, newAddTodoET;

    Bitmap bitmap;
    BitmapAdapter adapter;
    TodoAdapter todoAdapter;
    RecyclerView newTodoRV;
    ImageButton newAddTodoButton;

    String imageName, fullPath = "/data/user/0/com.appsbygreatness.ideasappformobiledevelopers/app_Images";
    ArrayList<String> imageNames;
    List<Todo> todos;
    IdeaRepository ideaRepository;

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

                deleteImagesFromDeviceStorage(fullPath, imageNames);
                killActivity();
            }
        });

        newAppName = findViewById(R.id.newAppName);
        newAppIdea = findViewById(R.id.newAppIdea);
        newFunctionality = findViewById(R.id.newFunctionality);
        newAddTodoButton = findViewById(R.id.newAddTodoButton);
        newAddTodoET = findViewById(R.id.newAddTodoET);


        RecyclerView newRV = findViewById(R.id.newRV);
        newTodoRV = findViewById(R.id.newTodoRV);


        imageNames = new ArrayList<>();
        todos = new ArrayList<>();



        adapter = new BitmapAdapter(fullPath, imageNames,this, this, this);
        todoAdapter = new TodoAdapter(todos, this, this, this);

        newRV.setAdapter(adapter);
        newRV.setLayoutManager(new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false));

        newTodoRV.setAdapter(todoAdapter);
        newTodoRV.setLayoutManager(new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false));

        newAddTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todos.add(new Todo(newAddTodoET.getText().toString(), false));
                newAddTodoET.setText("");
                todoAdapter.notifyDataSetChanged();
            }
        });

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

        Date date = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("dd-MM-YYYY, HH:mm:ss a", Locale.getDefault()).format(date);

        Idea idea = new Idea(appName, appIdea, functionality, todos, timeStamp, fullPath, imageNames);

        ideaRepository = new IdeaRepository(this);

        ideaRepository.insertIdea(idea);

        killActivity();

    }

    private void killActivity() {
        Intent intent = new Intent(getApplicationContext(), ViewIdeas.class);
        startActivity(intent);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_IMAGE_REQUESTCODE && data != null){

            Uri selectedImage = data.getData();

            try {

                //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                bitmap = scaleImageFromUri(this,selectedImage);

                SaveToInternalStorage saveObject = new SaveToInternalStorage();
                fullPath = saveObject.execute(bitmap).get();

                adapter.notifyDataSetChanged();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onBitmapClick(int position) {

        Intent intent = new Intent(getApplicationContext(), ViewBitmaps.class);

        intent.putExtra("Image path", fullPath);
        intent.putExtra("Image name", imageNames.get(position));

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
                        imageNames.remove(bitmapPosition);

                        adapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(),
                                "Picture deleted",
                                Toast.LENGTH_SHORT).show();

                    }
                }).show();

    }

    @Override
    public void onCompleteClick(int position) {
        boolean stateBeforeClick = todos.get(position).isCompleted();
        boolean stateAfterClick = !stateBeforeClick;
        todos.get(position).setCompleted(stateAfterClick);

        todoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteClick(int position) {
        todos.remove(position);
        todoAdapter.notifyDataSetChanged();
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

            fullPath = directory.getAbsolutePath();

            File mypath = new File(directory, (imageName));

            FileOutputStream fileOutputStream = null;
            Log.i("NewIdea", "About to commence save");

            try {
                fileOutputStream = new FileOutputStream(mypath);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                fileOutputStream.close();
                Log.i("NewIdea", "Save: Successful");

                //resizeBitmapInFile(mypath);

                return directory.getAbsolutePath();


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("NewIdea", "Save to internal storage: failed ");
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
    public void onBackPressed() {
        deleteImagesFromDeviceStorage(fullPath, imageNames);
        killActivity();
    }

    private void deleteImagesFromDeviceStorage(String imagePath, List<String> imageNames) {

        for(String imageName : imageNames){
            File f = new File(imagePath, imageName);

            try {
                f.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
