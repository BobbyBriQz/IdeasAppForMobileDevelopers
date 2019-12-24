package com.appsbygreatness.ideasappformobiledevelopers.model;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;

public class Idea {

    private String name;
    private String idea;
    private String functionality;
    private String todo;
    private String timestamp;

    private ArrayList<String> fullpaths;
    private ArrayList<String> imageNames;

    public Idea(String name, String idea, String functionality, String todo, String timestamp, ArrayList<String> fullpaths, ArrayList<String> imageNames) {
        this.name = name;
        this.idea = idea;
        this.functionality = functionality;
        this.todo = todo;
        this.timestamp = timestamp;
        //this.ideaPics = new ArrayList<>();
        this.fullpaths = new ArrayList<>();
        this.imageNames = new ArrayList<>();

        if(fullpaths != null && fullpaths.size() > 0) {

            this.fullpaths = fullpaths;
        }

        if(imageNames != null && imageNames.size() > 0){

            this.imageNames = imageNames;
        }

        /*if(bitmaps != null && bitmaps.size() > 0){

            this.ideaPics = bitmaps;
        }*/


    }

    /*public String getIdeaPic(int i) {
        return ideaPics.get(i);
    }

    public ArrayList<Bitmap> getIdeaPics() {
        return ideaPics;
    }

    public void addIdeaPic(Bitmap ideaPic) {

        *//*if (this.ideaPics == null){

            this.ideaPics = new ArrayList<>();
        }
*//*
        this.ideaPics.add(ideaPic);
    }*/

    public ArrayList<String> getFullpaths() {
        return fullpaths;
    }

    public void addFullpath(String fullpath) {

        this.fullpaths.add(fullpath);
    }


    public void setFullpaths(ArrayList<String> fullpaths) {
        this.fullpaths = fullpaths;
    }

    public ArrayList<String> getImageNames() {
        return imageNames;
    }

    public void addImageName(String imageName) {

        this.imageNames.add(imageName);
    }

    public void setImageNames(ArrayList<String> imageNames) {
        this.imageNames = imageNames;
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


}
