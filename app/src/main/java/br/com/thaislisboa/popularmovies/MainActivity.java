package br.com.thaislisboa.popularmovies;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.thaislisboa.popularmovies.domain.model.Movie;

public class MainActivity extends AppCompatActivity {

    private List<Movie> movies;

    private RecyclerView mRecyclerView;
    private String appKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        try {
            movies = savedInstanceState != null ?
                    (ArrayList) savedInstanceState.getSerializable("movies") :
                    new ArrayList<>();

            Bundle b = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            appKey = b.getString("appkey");
            mRecyclerView = findViewById(R.id.rv_main);
            int spanCount = getResources().getConfiguration().orientation;

            spanCount = spanCount == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

            if (savedInstanceState == null) {
                fetchMostPopular();
            } else {
                updateList();
            }
        } catch (Exception cause) {
            Log.e("MV", cause.getMessage(), cause);
        }
    }

    private void updateList() {
        updateList(null);
    }

    private void updateList(String action) {
        if (action != null) {
            new MovieAsyncTask().execute(action);
        } else {
            mRecyclerView.setAdapter(new MovieAdapter(movies));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movies", (ArrayList) movies);
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

    class MovieAsyncTask extends AsyncTask<String, Movie, List<Movie>> {

        @Override
        protected void onPostExecute(List<Movie> movies) {
            MainActivity.this.movies = movies;
            mRecyclerView.setAdapter(new MovieAdapter(movies));
            super.onPostExecute(movies);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {

            try {
                URL url = new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + strings[0])
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
                    Movie movie;

                    movies.clear();

                    for (int i = 0; i < results.length(); i++) {
                        json = results.getJSONObject(i);
                        movie = new Movie(json.getLong("id"),
                                json.getBoolean("video"),
                                json.getDouble("vote_average"),
                                json.getString("title"),
                                json.getString("poster_path"),
                                json.getString("backdrop_path"),
                                json.getString("overview"),
                                json.getString("release_date"));

                        movies.add(movie);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception cause) {
                Log.e("MV", cause.getMessage(), cause);
            }

            return movies;
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        MovieViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MovieAdapter extends RecyclerView.Adapter {

        private List<Movie> movies;

        MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.movie_item_view, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView mImageView = holder.itemView.findViewById(R.id.image);
            final Movie movie = movies.get(position);

            Picasso.with(MainActivity.this).load(movie.getPoster()).into(mImageView);

            mImageView.setOnClickListener(e -> {
                Intent i = new Intent(MainActivity.this, MovieDetailActivity.class);

                i.putExtra("movie", movie);

                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {

            return movies.size();
        }
    }

}




