package br.com.thaislisboa.popularmovies.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.thaislisboa.popularmovies.R;
import br.com.thaislisboa.popularmovies.domain.model.Movie;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Movie mMovie = null;

    public TrailerAdapter(@NonNull  Movie movie) {
        this.mMovie = movie;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item_view, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        TextView textView = holder.itemView.findViewById(R.id.tv_title);
        Context context = textView.getContext();

        textView.setText(mMovie.getTrailerName(position));

        holder.itemView.setOnClickListener((View e) -> {

            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mMovie.getTrailerYoutube(position)));

            context.startActivity(webIntent);

        });
    }

    @Override
    public int getItemCount() {
        return mMovie.getTrailersSize();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {
        TrailerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
