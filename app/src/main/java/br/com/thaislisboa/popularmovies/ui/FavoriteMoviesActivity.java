package br.com.thaislisboa.popularmovies.ui;


import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.data.MovieContract;
import br.com.thaislisboa.popularmovies.ui.adapter.MovieCursorAdapter;

public class FavoriteMoviesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

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

        mRecyclerView.setAdapter((mMovieAdapter));

        showLoading();


        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

    }


    private void showLoading() {

        mRecyclerView.setVisibility(View.INVISIBLE);

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

                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;


                return new CursorLoader(this,
                        movieQueryUri,
                        MovieContract.PROJ_MOVIE_LIST_PROJECTION,
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

        if (data.getCount() != 0) showMovieDataView();
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


        if (id == R.id.action1) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.KEY_SORT_ORDER, MainActivity.SORT_POPULAR);
            startActivity(i);
            return true;
        }

        if (id == R.id.action2) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.KEY_SORT_ORDER, MainActivity.SORT_TOP_RATED);
            startActivity(i);
            return true;
        }

        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}