package com.example.android.mymovie.helper;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mymovie.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.mymovie.helper.Utils.IMAGE_DOWNLOAD_URL;
import static com.example.android.mymovie.helper.Utils.IMAGE_SIZE;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Movie> moviesList;
    private ItemClickListener mClickListener;

    public MovieAdapter(Context mContext, ArrayList<Movie> moviesList) {
        this.mContext = mContext;
        this.moviesList = moviesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(moviesList.get(position));
        holder.clickListener(position);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public Movie getItemAtPosition(int position) {
        if (position <= moviesList.size() - 1 && position >= 0) {
            return moviesList.get(position);
        }
        return null;
    }

    public void addMovies(List<Movie> moviesList) {
        if (moviesList != null)
            this.moviesList = (ArrayList<Movie>) moviesList;
        else this.moviesList = new ArrayList<>();

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView movie_poster;
        TextView movie_title;
        CardView container;

        ViewHolder(View itemView) {
            super(itemView);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            movie_title = itemView.findViewById(R.id.movie_title);
            container = itemView.findViewById(R.id.container);

        }

        void clickListener(final int position) {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(position);
                }
            });
        }

        void bindData(Movie movie) {
            movie_title.setText(movie.title);
            Picasso.with(mContext)
                    .load(IMAGE_DOWNLOAD_URL + IMAGE_SIZE + movie.posterPath)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(movie_poster);

        }

    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }

}
