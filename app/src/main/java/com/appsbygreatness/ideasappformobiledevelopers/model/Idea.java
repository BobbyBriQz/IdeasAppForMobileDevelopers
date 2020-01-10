package com.appsbygreatness.ideasappformobiledevelopers.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.appsbygreatness.ideasappformobiledevelopers.ListConverter;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "idea_table")
public class Idea {

    @PrimaryKey( autoGenerate = true)
    private int id;

    private String name;

    private String idea;

    private String functionality;

    private String todo;

    private String timestamp;

    @ColumnInfo(name = "full_path")
    private String fullPath;

    @ColumnInfo(name = "image_names")
    @TypeConverters({ListConverter.class})
    private List<String> imageNames;

    public Idea(int id, String name, String idea, String functionality, String todo, String timestamp,
                String fullPath, List<String> imageNames) {
        this.id = id;
        this.name = name;
        this.idea = idea;
        this.functionality = functionality;
        this.todo = todo;
        this.timestamp = timestamp;

        this.fullPath = fullPath;
        this.imageNames = new ArrayList<>();



        if(imageNames != null && imageNames.size() > 0){

            this.imageNames = imageNames;
        }

    }


    public Idea(String name, String idea, String functionality, String todo, String timestamp,
                String fullpath, ArrayList<String> imageNames) {
        this.name = name;
        this.idea = idea;
        this.functionality = functionality;
        this.todo = todo;
        this.timestamp = timestamp;

        this.fullPath = fullpath;
        this.imageNames = new ArrayList<>();



        if(imageNames != null && imageNames.size() > 0){

            this.imageNames = imageNames;
        }

    }



    public String getFullpath() {
        return fullPath;
    }


    public List<String> getImageNames() {
        return imageNames;
    }

    public void addImageName(String imageName) {

        this.imageNames.add(imageName);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public String getFunctionality() {
        return functionality;
    }

    public void setFunctionality(String functionality) {
        this.functionality = functionality;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public void setFullPath(String fullPath) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setImageNames(ArrayList<String> imageNames) {
        this.imageNames = imageNames;
    }
}
