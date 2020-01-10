package com.appsbygreatness.ideasappformobiledevelopers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.appsbygreatness.ideasappformobiledevelopers.adapters.IdeaAdapter;
import com.appsbygreatness.ideasappformobiledevelopers.database.AppExecutors;
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
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ViewIdeas extends AppCompatActivity implements IdeaAdapter.OnIdeaClickListener,
        IdeaAdapter.OnLongIdeaClickListener, NavigationView.OnNavigationItemSelectedListener {

    //List of fields with global scope that will be used within class two or more class methods
    private List<Idea> ideas;
    private static IdeaAdapter ideaAdapter;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    TextView drawerUsername, drawerAppCount;
    View headerView;
    NavigationView navigationView;
    IdeaRepository ideaRepository;

    private static final String TAG = "ViewIdeas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ideas);

        //Link view, attach Listeners, etc.
        Toolbar appbar = findViewById(R.id.appBar);
        setSupportActionBar(appbar);

        drawerLayout = findViewById(R.id.drawerLayout);
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


        //setupPreferences() retrieve persisted data from the datastore and links to ideas list
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



    }

    public void updateHeaderAppCount(){

        drawerAppCount = headerView.findViewById(R.id.drawerAppsCount);
        drawerAppCount.setText(new StringBuilder().append("Currently working on ").append(ideas.size()).append(" apps."));
    }

    //Method that is called on return to onCreate from a previously visited activity
    //Pretty much just notifies the recyclerView's Adapter about a possible in the change in dataset
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == NewIdea.NEW_IDEAS_RESULTCODE ){

            ideaAdapter.notifyDataSetChanged();

        }else if(resultCode == DetailedPage.DETAILED_PAGE_RESULTCODE ){

            ideaAdapter.notifyDataSetChanged();

        }


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
                f.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Persist data to data-store
        savePreferences();
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

                ViewIdeas.ideaAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                ViewIdeas.ideaAdapter.getFilter().filter(s);
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


        if (item.getItemId() == R.id.saveApp){

            savePreferences();

        }else if (item.getItemId() == R.id.exitApp){

            savePreferences();

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

        savePreferences();

        Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(logout);

        finish();

    }

    public void savePreferences(){

        SaveInBackground saveInBackground = new SaveInBackground();

        saveInBackground.start();

    }


    //Links to data-store to retrieve persisted data, called in onCreate
    public void setupPreferences() {

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//        ArrayList<Idea> emptyArray = new ArrayList<>();
//        Gson gson = new Gson();
//        String emptyArrayOfIdeas = gson.toJson(emptyArray);
//
//        if (!preferences.getString("Ideas", emptyArrayOfIdeas).equals(emptyArrayOfIdeas)) {
//
//
//            String json = preferences.getString("Ideas", emptyArrayOfIdeas);
//
//            Type type = new TypeToken<ArrayList<Idea>>(){}.getType();
//
//            ArrayList<Idea> ideasToBeLoaded = gson.fromJson(json, type);
//
//            Singleton.getInstance().loadIdeas(ideasToBeLoaded);
//
//            ideas = Singleton.getInstance().getIdea();
//
//        }else{
//
//            ideas = Singleton.getInstance().getIdea();
//
//            //This adds a single app idea to the list of ideas if the list is empty is empty
//
//            ideas.add(new Idea("Sample App",
//
//                    "This mobile app helps developers document " +
//                    "their app ideas while also giving them the ability to write out the " +
//                    "core functionalities of the app and also set //todo tasks.",
//
//                    "Display app ideas in list.\n\n" +
//                            "App ideas could be added, modifies or deleted with ease.\n\n" +
//                            "Images can be imported for individual ideas, this is useful " +
//                            "especially in the case of multiple wireframes to be implemented, " +
//                            "complex app logic sketches or even UML diagrams\n\n" +
//                            "App allows you delete ideas that you have deemed unwanted.",
//
//                    "//Set up listview\n\n" +
//                            "Set up search functionality on listview.\n\n" +
//                            "Design app logo.\n\n" +
//                            "Remember to change font size for textView",
//
//                    "Since inception",
//                    null,
//                    null));
//
//        }

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

    //persists data to a background thread
    public class SaveInBackground extends Thread{

        @Override
        public void run() {
            super.run();

            SharedPreferences.Editor editor = preferences.edit();

            Gson gson = new Gson();

            String json = gson.toJson(ideas);

            editor.putString("Ideas", json).apply();

            Log.i(TAG, json);

        }
    }





}
