package com.appsbygreatness.ideasappformobiledevelopers.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;

import java.util.List;

@Dao
public interface IdeaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIdea(Idea idea);

    @Query("SELECT * from idea_table ORDER BY timestamp DESC")
    LiveData<List<Idea>> getAllIdeas();

    @Query("SELECT * FROM idea_table WHERE id = :id ")
    LiveData<Idea> getIdea(int id);

    @Query("DELETE FROM idea_table WHERE id = :id ")
    void deleteIdea(int id);

    @Delete
    void deleteIdea(Idea idea);

    @Update
    void updateIdea(Idea idea);

}
