package com.example.popularmovies2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.popularmovies2.models.DetailMovie;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM favorite_movies ORDER BY movie_release")
    List<FavoriteMovie> loadAllFavoriteMovies();

    @Insert
    void addFavoriteMovie(FavoriteMovie movie);

    @Delete
    void removeFavoriteMovie(FavoriteMovie taskEntry);

}
