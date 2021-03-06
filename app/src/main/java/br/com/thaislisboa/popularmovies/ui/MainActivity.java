package br.com.thaislisboa.popularmovies.ui;


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

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.model.Movie;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SORT_ORDER = "key_sort_order";
    public static final String SORT_POPULAR = "most_popular";
    public static final String SORT_TOP_RATED = "top_rated";

    private List<Movie> movies = new ArrayList<>();

    //private List<Movie> favorite;
    private RecyclerView mRecyclerView;
    private String appKey;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        mRecyclerView = findViewById(R.id.rv_main);
        int spanCount = getResources().getConfiguration().orientation;

        spanCount = spanCount == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        try {

            Bundle b = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            appKey = b.getString("appkey");

            if (savedInstanceState == null) {

                String sortOrder = SORT_POPULAR;
                Bundle bundle = getIntent().getExtras();
                if (bundle != null && bundle.containsKey(KEY_SORT_ORDER)) {
                    sortOrder = bundle.getString(KEY_SORT_ORDER);
                }
                if (SORT_TOP_RATED.equals(sortOrder)) {
                    fetchTopRated();
                } else {
                    fetchMostPopular();
                }
            } else {
                movies = (ArrayList) savedInstanceState.getSerializable("movies");
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
            if (id == R.id.favorite) {
                Intent i = new Intent(MainActivity.this, FavoriteMoviesActivity.class);
                startActivity(i);
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


    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

        private List<Movie> movies;

        MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.movie_item_view, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {

            final Movie movie = movies.get(position);

            String posterURL = Movie.getPosterURL(movie.getPosterPath());
            Picasso.with(MainActivity.this).load(posterURL).into(holder.mImageView);

            holder.mImageView.setOnClickListener(e -> {

                //fazer os if se o id do movie for igual aos ids da activity favorite, aparecer toast e nao adicionar
                Intent i = new Intent(MainActivity.this, MovieDetailActivity.class);
                i.putExtra("movie", movie);
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {

            return movies.size();
        }

        class MovieViewHolder extends RecyclerView.ViewHolder {

            ImageView mImageView;

            MovieViewHolder(View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.image);
            }
        }
    }
}




