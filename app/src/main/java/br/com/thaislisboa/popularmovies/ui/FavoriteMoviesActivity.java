package br.com.thaislisboa.popularmovies.ui;


import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.data.MovieContract;
import br.com.thaislisboa.popularmovies.ui.adapter.MovieCursorAdapter;

public class FavoriteMoviesActivity extends AppCompatActivity implements
       LoaderManager.LoaderCallbacks<Cursor>{


    private final String TAG = FavoriteMoviesActivity.class.getSimpleName();

    public static final String[] MOVIE_LIST_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            //MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER, //definir apenas o poster e o id
            //MovieContract.MovieEntry.COLUMN_VOTEAVERANGE,
            //MovieContract.MovieEntry.COLUMN_DATE,

    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_POSTER = 2;
    public static final int COLUMN_VOTEAVERANGE = 3;
    public static final int COLUMN_DATE = 4;

    private static final int ID_MOVIE_LOADER = 20;

    private MovieCursorAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        getSupportActionBar().setElevation(0f);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        mRecyclerView = findViewById(R.id.rv_main);
        int spanCount = getResources().getConfiguration().orientation;

        spanCount = spanCount == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieCursorAdapter(this);

          /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter((mMovieAdapter));

        showLoading();


        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

    }


    private void showLoading() {

        mRecyclerView.setVisibility(View.INVISIBLE);
         /* Finally, show the loading indicator */
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_MOVIE_LOADER:
              /* URI for all rows of weather data in our weather table */
                Uri moviecQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                //String mdate = MovieContract.MovieEntry.COLUMN_DATE;

                //String selection = MovieContract.MovieEntry.getSqlSelect();

                return new CursorLoader(this,
                        moviecQueryUri,
                        MOVIE_LIST_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader not implemented:" + loaderId);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieAdapter.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mRecyclerView.smoothScrollToPosition(mPosition);

       if(data.getCount() != 0) showMovieDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    private void showMovieDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        try {
            //order it by most popular
            if (id == R.id.action1) {

            }

            //order it by top rated
            if (id == R.id.action2) {

            }
            if (id == R.id.favorite) {

            }
        } catch (Exception cause) {
            Log.e("", cause.getMessage(), cause);
        }
        return true;
    }
}