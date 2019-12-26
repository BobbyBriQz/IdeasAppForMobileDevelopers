package com.appsbygreatness.ideasappformobiledevelopers.model;

import java.util.ArrayList;

public class Idea {

    private String name;
    private String idea;
    private String functionality;
    private String todo;
    private String timestamp;
    private String fullPath;

    private ArrayList<String> imageNames;

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


    public ArrayList<String> getImageNames() {
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
}
