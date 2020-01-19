package com.appsbygreatness.ideasappformobiledevelopers.activities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.appsbygreatness.ideasappformobiledevelopers.AppExecutors;
import com.appsbygreatness.ideasappformobiledevelopers.R;
import com.appsbygreatness.ideasappformobiledevelopers.adapters.IdeaAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.fragments.AboutDeveloperDialogFragment;
import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.repository.IdeaRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewIdeas extends AppCompatActivity implements IdeaAdapter.OnIdeaClickListener,
        IdeaAdapter.OnLongIdeaClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String DISPLAY_PICTURE_KEY = "Display picture";
    private static final String TAG = "ViewIdeas";

    //List of fields with global scope that will be used within two or more class methods
    private List<Idea> ideas;
    private IdeaAdapter ideaAdapter;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    private ActionBarDrawerToggle drawerToggle;
    TextView drawerUsername, drawerAppCount;
    CircleImageView circleImageView;
    View headerView;
    NavigationView navigationView;
    IdeaRepository ideaRepository;
    public static final int IMPORT_IMAGE_REQUEST_CODE = 998;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ideas);

        //Link view, attach Listeners, etc.
        Toolbar appbar = findViewById(R.id.appBar);
        setSupportActionBar(appbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navView);

        navigationView.setNavigationItemSelectedListener(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        //RecyclerView that displays the ideas
        recyclerView = findViewById(R.id.recyclerView);


        //setupPreferences() seta up shared preference and retrieves ideas list
        setupPreferences();



        //Fab that starts activity for the creation of a new idea
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), NewIdea.class);
                startActivity(intent);
                finish();
            }
        });


        //Drawer is set up
        NavigationView navigationView = findViewById(R.id.navView);
        headerView = navigationView.getHeaderView(0);

        drawerUsername = headerView.findViewById(R.id.drawerUserName);
        drawerUsername.setText(preferences.getString("Username", ""));

        circleImageView = headerView.findViewById(R.id.circularIV);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                importImage();
            }
        });

        if (!preferences.getString(DISPLAY_PICTURE_KEY, "").equals("")){
            circleImageView.setImageBitmap(getBitmapFromPreferences());
        }

    }

    private Bitmap getBitmapFromPreferences() {
        String displayPicture = preferences.getString(DISPLAY_PICTURE_KEY,"");
        File f = new File(displayPicture);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_IMAGE_REQUEST_CODE && data != null){

            Uri selectedImage = data.getData();
            Bitmap bitmap;

            try {

                //Reduce size of bitmap
                bitmap = scaleImageFromUri(this,selectedImage);

                //Save selected image to Local Storage
                SaveToInternalStorage saveObject = new SaveToInternalStorage();
                String displayPicturePath = saveObject.execute(bitmap).get();

                //Check to see if display picture has been selected before
                if (!preferences.getString(DISPLAY_PICTURE_KEY, "").equals("")){

                    String oldDisplayPicturePath = preferences.getString(DISPLAY_PICTURE_KEY, "");

                    deleteOldDPFromStorage(oldDisplayPicturePath);
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(DISPLAY_PICTURE_KEY, displayPicturePath);
                editor.apply();

                circleImageView.setImageBitmap(bitmap);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void deleteOldDPFromStorage(String oldDisplayPicturePath) {
        File f = new File(oldDisplayPicturePath);

        try{
            boolean deleteStatus = f.delete();

            if(deleteStatus){
                Toast.makeText(getApplicationContext(), "Display picture changed!",
                        Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.i("DisplayPic delete error", e.getMessage());
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
        final int REQUIRED_SIZE = 40;

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while (dbo.outWidth / scale / 2 >= REQUIRED_SIZE &&
                dbo.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
        }


        is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap srcBitmap = BitmapFactory.decodeStream(is, null, options);
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

    public void updateHeaderAppCount(){

        drawerAppCount = headerView.findViewById(R.id.drawerAppsCount);
        drawerAppCount.setText(new StringBuilder().append("Currently working on ").append(ideas.size()).append(" apps."));
    }


    //Starts an activity after an idea is clicked on in the recyclerView
    @Override
    public void onIdeaClick(int position) {

        Intent intent = new Intent(getApplicationContext(), DetailedPage.class);

        int id = ideas.get(position).getId();

        intent.putExtra("id", id);

        startActivity(intent);
        finish();


    }


    //Displays an alert dialog when idea is long pressed on the recyclerView
    @Override
    public void onLongIdeaClick(final int position) {
        //Set up functionality to delete

        final Idea ideaToBeDeleted = ideas.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete App Idea")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete "+ ideas.get(position).getName() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteImages(ideaToBeDeleted.getFullPath(),
                                        ideaToBeDeleted.getImageNames());

                        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {

                                //Delete idea
                                ideaRepository.deleteIdea(ideaToBeDeleted);

                            }
                        });

                        ideas.remove(position);

                        Toast.makeText(getApplicationContext(),
                                " App idea deleted",
                                Toast.LENGTH_SHORT).show();

                        updateHeaderAppCount();

                        ideaAdapter.notifyDataSetChanged();

                    }
                }).show();


    }

    private void deleteImages(String imagePath, List<String> imageNames) {

        for(String imageName : imageNames){
            File f = new File(imagePath, imageName);


            try {
                boolean status = f.delete();
                Log.i(TAG, "Delete status: " + status);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //code that inflates searchView for Toolbar
        getMenuInflater().inflate(R.menu.view_ideas_menu, menu);


        //set up code for search functionality
        MenuItem searchItem = menu.findItem(R.id.searchButton);

        //SearchManager searchManager = (SearchManager) IdeaList.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;

        if (searchItem != null){

            searchView = (SearchView) searchItem.getActionView();

        }

        assert searchView != null;
        searchView.isIconfiedByDefault();


        //Setting up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                ideaAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                ideaAdapter.getFilter().filter(s);
                return false;
            }});


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)){

            //The method called during the if check handles the opening of the drawer
            //Please do not delete this, future self
        }


        if (item.getItemId() == R.id.exitApp){

            logout();
            Intent exit = new Intent(Intent.ACTION_MAIN);
            exit.addCategory(Intent.CATEGORY_HOME);
            exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exit);

        }else if(item.getItemId() == R.id.logout){

            logout();
        }


        return super.onOptionsItemSelected(item);

    }

    public void logout(){

        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logout);

        finish();
    }



    //Initializes shared Preference and gets Ideas to be displayed
    public void setupPreferences() {

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        ideaRepository = new IdeaRepository(this);

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                ideas = ideaRepository.getAllIdeas();
                setUpRecyclerView();
                updateHeaderAppCount();
            }
        });



    }

    public void setUpRecyclerView(){

        //RecyclerView custom IdeaAdapter is created with the ideas list
        ideaAdapter = new IdeaAdapter(this, ideas, this, this);

        //Adapter is attached to the recyclerView instance and preferred Layout is set
        recyclerView.setAdapter(ideaAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


            if (menuItem.getItemId() == R.id.logoutDrawer){

                logout();

            }else if(menuItem.getItemId() == R.id.changePassword){


                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

            }else if (menuItem.getItemId() == R.id.suggest){


            }else if (menuItem.getItemId() == R.id.aboutDeveloperDialogFragment){

                //Creates the about developer fragment

                AboutDeveloperDialogFragment aboutDeveloperDialogFragment = new AboutDeveloperDialogFragment();

                aboutDeveloperDialogFragment.show(getSupportFragmentManager(),"");


            }else if(menuItem.getItemId() == R.id.rateApp){


            }else if(menuItem.getItemId() == R.id.removeAds){


            }

        return false;

    }

    public class SaveToInternalStorage extends AsyncTask<Bitmap, Void, String> {

        String imageName;
        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];

            Date date = Calendar.getInstance().getTime();
            imageName = new SimpleDateFormat("dd-MM-YYYY, HH:mm:ss a", Locale.getDefault()).format(date) + "jpg";


            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            File directory = cw.getDir("Images", Context.MODE_PRIVATE);


            File mypath = new File(directory, (imageName));

            String imagePath = mypath.getAbsolutePath();

            FileOutputStream fileOutputStream;
            Log.i("NewIdea", "About to commence save");

            try {
                fileOutputStream = new FileOutputStream(mypath);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                fileOutputStream.close();
                Log.i("NewIdea", "Save: Successful");



                return imagePath;


            } catch (Exception e) {
                e.printStackTrace();
                Log.i("NewIdea", "Save to internal storage: failed ");
            }

            return null;
        }
    }


}
