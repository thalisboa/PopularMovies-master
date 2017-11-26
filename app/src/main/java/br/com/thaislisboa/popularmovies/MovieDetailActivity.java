package br.com.thaislisboa.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.thaislisboa.popularmovies.domain.model.Movie;
import br.com.thaislisboa.popularmovies.domain.model.Trailer;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;

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

        mTitle.setText(movie.getTitle());
        Picasso.with(this).load(movie.getPoster()).into(mPicture);
        mDetails.setText(movie.getOverview());
        mYear.setText(movie.getYear());

        mGrade.setText(movie.getGrade());
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {
        TrailerViewHolder(View itemView) {
            super(itemView);
        }
    }

    class TrailerAdapter extends RecyclerView.Adapter {

        private List<Trailer> trailers;

        TrailerAdapter(List<Trailer> trailers) {
            this.trailers = trailers;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MovieDetailActivity.this)
                    .inflate(R.layout.trailer_item_view, parent, false);
            return new TrailerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView mImageView = holder.itemView.findViewById(R.id.iv_play);
            TextView mTextView = holder.itemView.findViewById(R.id.tv_title);
        }

        @Override
        public int getItemCount() {
            return trailers.size();
        }
    }
}
