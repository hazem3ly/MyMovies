package com.example.android.mymovie.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mymovie.R;
import com.example.android.mymovie.database.MovieContract;
import com.example.android.mymovie.helper.Movie;
import com.example.android.mymovie.helper.MovieReviews;
import com.example.android.mymovie.helper.MovieVideos;
import com.example.android.mymovie.network.RestClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.mymovie.helper.Utils.IMAGE_DOWNLOAD_URL;
import static com.example.android.mymovie.helper.Utils.IMAGE_SIZE;
import static com.example.android.mymovie.helper.Utils.MOVIE_DETAILS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {


    private Movie selectedMovie;
    private ImageView app_bar_image, poster;
    private View rootView;
    private AsyncTask task;
    private Boolean isFav;

    public DetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        if (data.getParcelable(MOVIE_DETAILS) != null) {
            selectedMovie = data.getParcelable(MOVIE_DETAILS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.windowBackground));

        app_bar_image = rootView.findViewById(R.id.app_bar_image);
        poster = rootView.findViewById(R.id.poster);

        if (selectedMovie != null) {
            updateData();
        }


        return rootView;
    }

    private void updateData() {

        Picasso.get()
                .load(IMAGE_DOWNLOAD_URL + "w500" + selectedMovie.backdropPath)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(app_bar_image);

        Picasso.get()
                .load(IMAGE_DOWNLOAD_URL + IMAGE_SIZE + selectedMovie.posterPath)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(poster);
        CollapsingToolbarLayout appBarLayout = rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout.setTitle(selectedMovie.title);
        appBarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT));


        task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                String whereClause = MovieContract.MovieEntry.COLUMN_ID + " = ?";
                String[] whereArgs = new String[]{String.valueOf(selectedMovie.id)};

                Cursor data = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        whereClause,
                        whereArgs,
                        null);
                boolean f = false;
                if (data != null) {
                    f = data.getCount() > 0;
                    data.close();
                }
                return f;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                isFav = result;
                if (result)
                    ((FloatingActionButton) rootView.findViewById(R.id.fav_fab)).setImageResource(R.drawable.ic_favorite);
                else
                    ((FloatingActionButton) rootView.findViewById(R.id.fav_fab)).setImageResource(R.drawable.ic_favorite_border);
            }
        }.execute();

        (rootView.findViewById(R.id.fav_fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFav) {
                    removeFav();
                } else addFav();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////
        ((TextView) rootView.findViewById(R.id.movie_title)).setText(selectedMovie.title);
        ((TextView) rootView.findViewById(R.id.release_date)).setText(selectedMovie.releaseDate);
        ((TextView) rootView.findViewById(R.id.rate)).setText(selectedMovie.voteAverage + getString(R.string.over_ten));
        ((TextView) rootView.findViewById(R.id.description)).setText(selectedMovie.overview);


        getVideosAndReviews();

    }

    private void getVideosAndReviews() {
        Call<MovieVideos> moviesApiCall = new RestClient().
                getApi_service().getVideos(String.valueOf(selectedMovie.id), "39dbc8225484ac7c6ceca6ff3701b74b");
        moviesApiCall.enqueue(new Callback<MovieVideos>() {
            @Override
            public void onResponse(@NonNull Call<MovieVideos> call, @NonNull Response<MovieVideos> response) {
                if (response.body() != null) {
                    final MovieVideos moviesApi = response.body();
                    if (moviesApi != null && moviesApi.results != null && moviesApi.results.size() >= 0) {

                        if (getContext() != null) fillVideosList(moviesApi.results);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieVideos> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        Call<MovieReviews> movieReviews = new RestClient().
                getApi_service().getReviews(String.valueOf(selectedMovie.id), "39dbc8225484ac7c6ceca6ff3701b74b");
        movieReviews.enqueue(new Callback<MovieReviews>() {
            @Override
            public void onResponse(@NonNull Call<MovieReviews> call, @NonNull Response<MovieReviews> response) {
                if (response.body() != null) {
                    MovieReviews moviesApi = response.body();
                    if (moviesApi != null && moviesApi.reviewsList != null && moviesApi.reviewsList.size() >= 0) {

                        if (getContext() != null) fillReviewsList(moviesApi.reviewsList);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieReviews> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void fillReviewsList(List<MovieReviews.Reviews> reviewsList) {
        if (reviewsList == null) return;
        if (reviewsList.size() == 0) {
            TextView textView = new TextView(getContext());
            textView.setText(R.string.no_reviews);
            ((LinearLayout) rootView.findViewById(R.id.reviews_list)).addView(textView);
        }
        for (final MovieReviews.Reviews reviews : reviewsList) {
            TextView textView = new TextView(getContext());
            textView.setText(reviews.author + " :" + reviews.content);
            ((LinearLayout) rootView.findViewById(R.id.reviews_list)).addView(textView);
        }
    }

    private void fillVideosList(List<MovieVideos.Videos> results) {
        if (results == null) return;

        if (results.size() == 0) {
            TextView textView = new TextView(getContext());
            textView.setText(R.string.no_trailers);
            ((LinearLayout) rootView.findViewById(R.id.videos_list)).addView(textView);
        }
        for (final MovieVideos.Videos videos : results) {
            ImageView button = new ImageView(getContext());
//            View view = new View(getContext());
//            RelativeLayout.LayoutParams v = new RelativeLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT);
//            view.setLayoutParams(v);
//            button.setText(videos.name);
//            https://img.youtube.com/vi/D42_74mOPYk/default.jpg
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            layoutParams.rightMargin = 30;
            button.setLayoutParams(layoutParams);
            Picasso.get()
                    .load("https://img.youtube.com/vi/" + videos.key + "/default.jpg")
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .resize(850,500)
                    .into(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videos.key)));
                    Toast.makeText(getContext(), videos.name, Toast.LENGTH_SHORT).show();
                }
            });
            ((LinearLayout) rootView.findViewById(R.id.videos_list)).addView(button);
//            ((LinearLayout) rootView.findViewById(R.id.videos_list)).addView(view);
        }
    }

    private void addFav() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ID, selectedMovie.id);
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER, selectedMovie.posterPath);
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, selectedMovie.voteAverage);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, selectedMovie.releaseDate);
        cv.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, selectedMovie.overview);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, selectedMovie.title);
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP, selectedMovie.backdropPath);

        Uri uri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        if (uri != null) {
            Toast.makeText(getContext(), "Added To Favourite", Toast.LENGTH_SHORT).show();
            ((FloatingActionButton) rootView.findViewById(R.id.fav_fab)).setImageResource(R.drawable.ic_favorite);
            isFav = true;

        }

    }

    private void removeFav() {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(selectedMovie.id)).build();
        int itemDeleted = getActivity().getContentResolver().delete(uri, null, null);
        if (itemDeleted > 0) {
            Toast.makeText(getContext(), "Removed From Favourite", Toast.LENGTH_SHORT).show();
            ((FloatingActionButton) rootView.findViewById(R.id.fav_fab)).setImageResource(R.drawable.ic_favorite_border);
            isFav = false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (task != null) task.cancel(true);
    }
}
