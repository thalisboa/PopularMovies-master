package br.com.thaislisboa.popularmovies.ui.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.data.MovieContract;


public class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private Object context;

    public MovieCursorAdapter(Context mContext){
        this.mContext = mContext;

    }

    //param holder The ViewHolder to bind Cursor data to

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_item_view, parent,false);

        return new MovieViewHolder(view);
    }


   @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

       // get to the right location in the cursor
       mCursor.moveToPosition(position);

        // Get the id index
       int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
       int id = mCursor.getInt(idIndex);

        // Get the poster column index
        int columnIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        // Based on the index, get the real data
        String poster = mCursor.getString(columnIndex);

        Picasso.with(this.mContext).load(poster).into(holder.posterImageView);

        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;
        }

        return mCursor.getCount();
    }
    // check if this cursor is the same as the previous cursor (mCursor)
    public Cursor swapCursor(Cursor c){
        if( mCursor == c){
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        //check if this is a valid cursor, then update the cursor

        if(c != null){
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

