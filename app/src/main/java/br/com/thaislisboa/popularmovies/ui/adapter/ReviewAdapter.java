package br.com.thaislisboa.popularmovies.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.model.Movie;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Movie mMovie;

    public ReviewAdapter(Movie movie) {
        this.mMovie = movie;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_view, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        TextView mTextView1 = holder.itemView.findViewById(R.id.tv_name_review);
        TextView mTextView2 = holder.itemView.findViewById(R.id.tv_comment_review);

        mTextView1.setText(mMovie.getReviewName(position));
        mTextView2.setText(mMovie.getReviewComment(position));
    }

    @Override
    public int getItemCount() {
        return mMovie.getReviewsSize();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ReviewViewHolder(View itemView) {
            super(itemView);
        }
    }
}
