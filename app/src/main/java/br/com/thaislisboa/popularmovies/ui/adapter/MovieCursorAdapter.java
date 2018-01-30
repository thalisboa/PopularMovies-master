package br.com.thaislisboa.popularmovies.ui.adapter;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.data.MovieContract;
import br.com.thaislisboa.popularmovies.domain.model.Movie;
import br.com.thaislisboa.popularmovies.ui.MovieDetailActivity;


public class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public MovieCursorAdapter(Context mContext) {
        this.mContext = mContext;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_item_view, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieCursorAdapter.MovieViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        final int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        final int id = mCursor.getInt(idIndex);

        int columnIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);


        String poster = mCursor.getString(columnIndex);

        String posterURL = Movie.getPosterURL(poster);
        Picasso.with(mContext).load(posterURL).into(holder.posterImageView);

        holder.itemView.setTag(id);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMovieDetails(id);
            }
        });
    }


    private void showMovieDetails(long id) {

        ContentResolver resolver = mContext.getContentResolver();

        Cursor cursor = resolver.query(MovieContract.MovieEntry.buildMovieUriWithId(id),
                MovieContract.PROJ_MOVIE_LIST_DETAILS, null, null, null);


        if (cursor != null) {

            cursor.moveToFirst();

            Movie movie = new Movie();

            long movieId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
            String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            String date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE));
            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            double vote = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTEAVERANGE));

            movie.setMovieId(movieId);
            movie.setPoster(poster);
            movie.setDate(date);
            movie.setTitle(title);
            movie.setOverview(overview);
            movie.setVoteAverage(vote);

            cursor.close();


            Intent intent = new Intent(mContext, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            mContext.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;


        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView posterImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.image);
        }
    }
}


