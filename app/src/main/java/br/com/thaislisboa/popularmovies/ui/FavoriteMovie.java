package br.com.thaislisboa.popularmovies.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import br.com.thaislisboa.popularmovies.R;

public class FavoriteMovie extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
    }


    private void updateList() {
        updateList(null);
    }

    private void updateList(String action) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        try {
            //order it by most popular
            if (id == R.id.action1) {
                fetchMostPopular();
            }

            //order it by top rated
            if (id == R.id.action2) {
                fetchTopRated();
            }
            if (id == R.id.favorite) {

            }
        } catch (Exception cause) {
            Log.e("", cause.getMessage(), cause);
        }
        return true;
    }

    private void fetchTopRated() throws Exception {
        updateList("top_rated");
    }

    private void fetchMostPopular() throws Exception {
        updateList("popular");
    }

    private void fetchFavorite() throws Exception {
        updateList("favorite");
    }


}
