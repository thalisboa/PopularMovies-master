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


public abstract class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.MovieViewHolder> {

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

        // get to the right location in the cursor
        mCursor.moveToPosition(position);

        // Get the id index
        final int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        final int id = mCursor.getInt(idIndex);

        // Get the poster column index
        int columnIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        // Based on the index, get the real data
        String poster = mCursor.getString(columnIndex);

        Picasso.with(mContext).load(poster).into(holder.posterImageView);

        holder.itemView.setTag(id);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMovieDetails(id);
            }
        });
    }


    private void showMovieDetails(int movieId) {

        // 1 - Pegar o content resolver

        ContentResolver resolver = mContext.getContentResolver();

        // 2 - chamar o metodo query com a projecao COMPLETA, passando o id do filme como argumentos.

        Cursor cursor = resolver.query(MovieContract.MovieEntry.buildMovieUriWithId(movieId),
                MovieContract.PROJ_MOVIE_LIST_DETAILS, null, null, null);

        // 3 - Com o cursor de resultado, pegar os detalhes do filme
        // 4 - colocar os detalhes em um objeto Movie()

        if (cursor != null) {

            cursor.moveToFirst();

            Movie movie = new Movie();

            String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
            String date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE));
            String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            double vote = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTEAVERANGE));

            movie.setId(movieId);
            movie.setPoster(poster);
            movie.setDate(date);
            movie.setTitle(title);
            movie.setOverview(overview);
            movie.setVoteAverage(vote);

            cursor.close();

            // 5 - enviar esse movie dentro de um intent para a ativide de detalhes
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

    // check if this cursor is the same as the previous cursor (mCursor)
    public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        //check if this is a valid cursor, then update the cursor

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    // Called when ViewHolders are created to fill a RecyclerView.

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView posterImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.image);
        }
    }
}


