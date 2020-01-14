package com.appsbygreatness.ideasappformobiledevelopers.activities;

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
import android.widget.ImageButton;
import android.widget.Toast;

import com.appsbygreatness.ideasappformobiledevelopers.R;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.BitmapAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.TodoAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.model.Todo;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        Intent saveNewIdea = new Intent(getApplicationContext(), ViewIdeas.class);
        saveNewIdea.putExtra("position", 0);

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

                return directory.getAbsolutePath();


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("NewIdea", "Save to internal storage: failed ");
            }

            return null;
        }
    }

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
