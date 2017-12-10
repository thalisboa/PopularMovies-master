package br.com.thaislisboa.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import br.com.thaislisboa.popularmovies.domain.model.Movie;

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

        mTitle.setText(movie.getTitle());
        Picasso.with(this).load(movie.getPoster()).into(mPicture);
        mDetails.setText(movie.getOverview());
        mYear.setText(movie.getYear());

        mGrade.setText(movie.getGrade());

        mRecyclerViewTrailer = findViewById(R.id.rv_trailers);
        mRecyclerViewReview = findViewById(R.id.rv_reviews);

        mRecyclerViewTrailer.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerViewReview.setLayoutManager(new LinearLayoutManager(this));

        try {

            Bundle b = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            appKey = b.getString("appkey");

        } catch (Exception cause) {
            Log.e("MV", cause.getMessage(), cause);

        }

        new TrailerAsyncTask().execute(movie);
        new ReviewAsyncTask().execute(movie);
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
                URL url = new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + movies[0].getId() + "/videos")
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

    class TrailerViewHolder extends RecyclerView.ViewHolder {
        TrailerViewHolder(View itemView) {
            super(itemView);
        }
    }

    class TrailerAdapter extends RecyclerView.Adapter {

        private Movie movie;


        TrailerAdapter(Movie movie) {
            this.movie = movie;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MovieDetailActivity.this)
                    .inflate(R.layout.trailer_item_view, parent, false);
            return new TrailerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView mTextView = holder.itemView.findViewById(R.id.tv_title);
            holder.itemView.setOnClickListener((View e) -> {


                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(movie.getTrailerYoutube(position)));

                startActivity(webIntent);

            });


            mTextView.setText(movie.getTrailerName(position));

        }

        @Override
        public int getItemCount() {
            return movie.getTrailersSize();
        }
    }
        //REVIEW// B.O TA AQUI

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
                    String urls = "http://api.themoviedb.org/3/movie/" + movie.getId() + "/reviews";

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

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ReviewViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ReviewAdapter extends RecyclerView.Adapter {

        private Movie movie;

        ReviewAdapter(Movie movie) {
            this.movie = movie;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MovieDetailActivity.this)
                    .inflate(R.layout.review_item_view, parent, false);
            return new ReviewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView mTextView1 = holder.itemView.findViewById(R.id.tv_name_review);
            TextView mTextView2 = holder.itemView.findViewById(R.id.tv_comment_review);

            mTextView1.setText(movie.getReviewName(position));
            mTextView2.setText(movie.getReviewComment(position));

        }

        @Override
        public int getItemCount() {
            return movie.getReviewsSize();
        }
    }
}