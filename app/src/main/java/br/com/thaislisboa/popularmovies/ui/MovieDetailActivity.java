package br.com.thaislisboa.popularmovies.ui;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private Movie movie;
    private RecyclerView mRecyclerViewTrailer, mRecyclerViewReview;
    private String appKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_view);


        movie = (Movie) getIntent().getSerializableExtra("movie");

        // movie.addTrailer("Teste1", "");

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


        new TrailerAsyncTask().execute(movie);
        new ReviewAsyncTask().execute(movie);
    }


    public void addMovieToFavorites(View view) {

        movie = (Movie) getIntent().getSerializableExtra("movie");

        // Put the movie's details inside the ContentValues

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTEAVERANGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());

        // Get my provider via ContentResolver and CONTENT_URI

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        // Call method insert from provider passing the content values


        // if everything is ok, show a toast.
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();


        }
        finish();

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