package com.appsbygreatness.ideasappformobiledevelopers;

import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

import java.util.ArrayList;


public class Singleton {

    private static Singleton instance;

    private ArrayList<Idea> ideas;

    private Singleton(){
        ideas = new ArrayList<>();
    }

    public static Singleton getInstance(){

        if (instance == null){

            instance = new Singleton();
        }
        return instance;
    }

    public ArrayList<Idea> getIdea(){

        return this.ideas;
    }

    public void loadIdeas(ArrayList<Idea> ideas){

        this.ideas = ideas;

    }



}
