package br.com.thaislisboa.popularmovies.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.data.MovieContract;
import br.com.thaislisboa.popularmovies.domain.model.Movie;
import br.com.thaislisboa.popularmovies.ui.adapter.ReviewAdapter;
import br.com.thaislisboa.popularmovies.ui.adapter.TrailerAdapter;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String STATE_KEY_MOVIE = "current_movie";

    private Movie movie;
    private RecyclerView mRecyclerViewTrailer, mRecyclerViewReview;
    private String appKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_view);

        movie = (Movie) getIntent().getSerializableExtra("movie");


        TextView mTitle = findViewById(R.id.title);
        ImageView mPicture = findViewById(R.id.iv_picture);
        TextView mYear = findViewById(R.id.tv_year);
        TextView mDetails = findViewById(R.id.tv_details);
        TextView mGrade = findViewById(R.id.tv_grade);
        //ImageView mStar = findViewById(R.id.iv_star);

        mTitle.setText(movie.getTitle());
        String posterURL = Movie.getPosterURL(movie.getPosterPath());
        Picasso.with(this).load(posterURL).into(mPicture);
        mDetails.setText(movie.getOverview());
        mYear.setText(movie.getYear());

        mGrade.setText(movie.getGrade());
        ToggleButton toggle = findViewById(R.id.favorite);

        mRecyclerViewTrailer = findViewById(R.id.rv_trailers);
        mRecyclerViewReview = findViewById(R.id.rv_reviews);

        mRecyclerViewTrailer.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewReview.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerViewTrailer.setNestedScrollingEnabled(false);
        mRecyclerViewReview.setNestedScrollingEnabled(false);

        try {

            Bundle b = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            appKey = b.getString("appkey");

        } catch (Exception cause) {
            Log.e("MV", cause.getMessage(), cause);

        }


        boolean isFavorite = isMovieFavorite(movie.getMovieId());
        toggle.setChecked(isFavorite);


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean isSuccess = addMovieToFavorites();
                    if (isSuccess) {

                        Toast.makeText(MovieDetailActivity.this, "Added to favorites!",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(MovieDetailActivity.this, "Couldn't add to favorites!",
                                Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                    }
                } else {

                    boolean isSuccess = removeFromFavorites();
                    if (isSuccess) {

                        Toast.makeText(MovieDetailActivity.this, "Removed from favorites!",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(MovieDetailActivity.this, "Couldn't remove from favorites!",
                                Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(true);
                    }
                }
            }
        });

        if ((savedInstanceState != null) && savedInstanceState.containsKey(STATE_KEY_MOVIE)) {
            movie = (Movie) savedInstanceState.getSerializable(STATE_KEY_MOVIE);
        }
        else {
            new TrailerAsyncTask().execute(movie);
            new ReviewAsyncTask().execute(movie);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        // Save the movie to restore it later
        outState.putSerializable(STATE_KEY_MOVIE, movie);
    }

    private boolean removeFromFavorites() {

        ContentResolver contentResolver = getContentResolver();

        if (contentResolver != null) {
            int rows = contentResolver.delete(MovieContract.MovieEntry
                    .buildMovieUriWithId(movie.getMovieId()), null, null);
            return (rows > 0);
        }

        return false;
    }

    public boolean addMovieToFavorites() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry._ID, movie.getMovieId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTEAVERANGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());


        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        return (uri != null);
    }

    private boolean isMovieFavorite(long id) {

        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.buildMovieUriWithId(id),
                MovieContract.PROJ_MOVIE_LIST_PROJECTION, null, null, null);

        try {
            return (cursor != null && cursor.moveToFirst());
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    class TrailerAsyncTask extends AsyncTask<Movie, Movie, Movie> {

        @Override
        protected void onPostExecute(Movie movie) {
            MovieDetailActivity.this.movie = movie;
            mRecyclerViewTrailer.setAdapter(new TrailerAdapter(movie));
            super.onPostExecute(movie);
        }

        @Override
        protected Movie doInBackground(Movie... movies) {

            try {
                URL url = new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + movies[0].getMovieId() + "/videos")
                        .buildUpon()
                        .appendQueryParameter("api_key", appKey)
                        .build().toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                StringBuilder content = new StringBuilder();
                try (InputStream in = urlConnection.getInputStream();
                     Scanner data = new Scanner(in)) {
                    data.useDelimiter("\n");
                    while (data.hasNext()) {
                        content.append(data.next());
                    }

                    JSONObject json = new JSONObject(content.toString());
                    JSONArray results = json.getJSONArray("results");

                    // First clear the current trailers
                    movie.getTrailers().clear();;

                    for (int i = 0; i < results.length(); i++) {
                        json = results.getJSONObject(i);

                        movie.addTrailer(json.getString("name"),
                                json.getString("key"));
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception cause) {
                Log.e("MV", cause.getMessage(), cause);
            }

            return movie;
        }
    }


    class ReviewAsyncTask extends AsyncTask<Movie, Movie, Movie> {

        @Override
        protected void onPostExecute(Movie movie) {
            MovieDetailActivity.this.movie = movie;
            mRecyclerViewReview.setAdapter(new ReviewAdapter(movie));
            super.onPostExecute(movie);
        }

        @Override
        protected Movie doInBackground(Movie... movies) {
            HttpURLConnection urlConnection;

            movie = movies[0];

            try {
                URL url;
                String urls = "http://api.themoviedb.org/3/movie/" + movie.getMovieId() + "/reviews";

                url = new URL(Uri.parse(urls)
                        .buildUpon()
                        .appendQueryParameter("api_key", appKey)
                        .build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    StringBuilder content = new StringBuilder();
                    try (InputStream in = urlConnection.getInputStream();
                         Scanner data = new Scanner(in)) {
                        data.useDelimiter("\n");
                        while (data.hasNext()) {
                            content.append(data.next());
                        }

                        JSONObject json = new JSONObject(content.toString());
                        JSONArray results = json.getJSONArray("results");

                        // First clear the current reviews
                        movie.getReviews().clear();

                        for (int i = 0; i < results.length(); i++) {
                            json = results.getJSONObject(i);

                            movie.addReview(json.getString("author"),
                                    json.getString("content"));
                        }
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception cause) {
                Log.e("MV", cause.getMessage(), cause);
            }

            return movie;
        }

    }
}