package com.example.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.popularmovies2.adapters.MoviesAdapter;
import com.example.popularmovies2.database.AppDatabase;
import com.example.popularmovies2.database.FavoriteMovie;
import com.example.popularmovies2.databinding.ActivityMainBinding;
import com.example.popularmovies2.models.Movie;
import com.example.popularmovies2.utilities.AsyncTaskCompleteListener;
import com.example.popularmovies2.utilities.FetchAsyncTaskBase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterListItemClickListener, AsyncTaskCompleteListener {

    public static final String POPULAR_QUERY = "popular";
    public static final String TOP_RATED_QUERY = "top_rated";

    private static Bundle mBundleRecyclerViewState;
    private Movie[] mMovies;

    private MoviesAdapter mMoviesAdapter;

    ActivityMainBinding mMainBinding;

    private AppDatabase mDb;

    String query = "popular";
    private final String KEY_RECYCLER_STATE = "recycler_state";
    public static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LIFECYCLE_CALLBACKS_TEXT_KEY);
        }
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        GridLayoutManager LayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));

        mMainBinding.recyclerViewMovies.setLayoutManager(LayoutManager);
        mMainBinding.recyclerViewMovies.setHasFixedSize(true);

        mMainBinding.recyclerViewMovies.setAdapter(mMoviesAdapter);

        loadMovieData(query);
    }

    @Override
    public void onListItemClick(int item) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("Movie", mMovies[item]); // Send the movie Object as Parcelable
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sort_most_popular){
            query = POPULAR_QUERY;
            loadMovieData(POPULAR_QUERY);
        }else if (id == R.id.sort_highest_rated) {
            query = TOP_RATED_QUERY;
            loadMovieData(TOP_RATED_QUERY);
        }else{
            loadFavoritePostersToGrid();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskComplete(Object movies) {
        loadPostersToGrid((Movie[]) movies);
    }


    // Helper methods

    private void loadMovieData(String query){
        mMainBinding.progressBar.setVisibility(View.VISIBLE);
        showMoviesList();

        FetchAsyncTaskBase getMovies = new FetchAsyncTaskBase(query, this);
        getMovies.execute();
    }

    private void loadFavoritePostersToGrid() {
        mDb = AppDatabase.getInstance(getApplicationContext());
        FavoriteMovie[] favoriteMoviesArray = mDb.taskDao().loadAllFavoriteMovies();
        List<Movie> moviesList = new ArrayList<>();
        for (int i = 0; favoriteMoviesArray.length -1 > i; i++)
            moviesList.add(new Movie(null, favoriteMoviesArray[i].getMoviePoster()));
        loadPostersToGrid(moviesList.toArray(new Movie[0]));
    }

    private void showMoviesList() {
        mMainBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMainBinding.recyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMainBinding.recyclerViewMovies.setVisibility(View.INVISIBLE);
        mMainBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // Following this thread: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
    // The best way to calculate the number of the columns that will be displayed
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    private void loadPostersToGrid(Movie[] movies) {
        mMainBinding.progressBar.setVisibility(View.INVISIBLE);
        if (movies != null) {
                showMoviesList();
                mMoviesAdapter = new MoviesAdapter(movies, MainActivity.this);
                mMainBinding.recyclerViewMovies.setAdapter(mMoviesAdapter);
                mMovies = movies;
            } else {
                showErrorMessage();
            }
    }


    //Lifecycle methods

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        query = savedInstanceState.getString(LIFECYCLE_CALLBACKS_TEXT_KEY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String lifecycleSortBy = query;
        outState.putString(LIFECYCLE_CALLBACKS_TEXT_KEY, lifecycleSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mMainBinding.recyclerViewMovies.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mMainBinding.recyclerViewMovies.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

}
