package com.example.android.mymovie.fragments;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mymovie.R;
import com.example.android.mymovie.database.MovieContract;
import com.example.android.mymovie.helper.Movie;
import com.example.android.mymovie.helper.MovieAdapter;
import com.example.android.mymovie.helper.MoviesApi;
import com.example.android.mymovie.helper.PageChangeListener;
import com.example.android.mymovie.network.RestClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.mymovie.activities.MainActivity.movieSelectListener;
import static com.example.android.mymovie.helper.Utils.MOVIES_LIST_INSTANCE_KEY;
import static com.example.android.mymovie.helper.Utils.POSITION;
import static com.example.android.mymovie.helper.Utils.RECYCLER_POSITION;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements MovieAdapter.ItemClickListener {

    private int viewPagerPagePosition = -1;

    private RecyclerView movies_recycle_view;
    private ArrayList<Movie> moviesList;
    private GridLayoutManager layoutManager;
    private MovieAdapter movieAdapter;
    private TextView info_text;
    public static PageChangeListener pageChangeListener;
    private AsyncTask task;
    private boolean isSaved;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle data = getArguments();
            viewPagerPagePosition = data.getInt(POSITION, 0);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (moviesList != null)
            outState.putParcelableArrayList(MOVIES_LIST_INSTANCE_KEY, moviesList);
        if (layoutManager != null)
            outState.putParcelable(RECYCLER_POSITION, layoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        info_text = rootView.findViewById(R.id.info_text);

        int columns = getResources().getInteger(R.integer.grid_columns);
        layoutManager = new GridLayoutManager(getContext(), columns);

        moviesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(getContext(), moviesList);
        movieAdapter.setClickListener(this);

        movies_recycle_view = rootView.findViewById(R.id.movies_list);
        movies_recycle_view.setLayoutManager(layoutManager);
        movies_recycle_view.setAdapter(movieAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIES_LIST_INSTANCE_KEY)) {
                moviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST_INSTANCE_KEY);
                if (moviesList == null || moviesList.size() == 0) {
                    getMovies();
                } else {
                    isSaved = true;
                    info_text.setVisibility(View.GONE);
                    movies_recycle_view.setVisibility(View.VISIBLE);
                    movieAdapter.addMovies(moviesList);
                }

            }

            if (savedInstanceState.containsKey(RECYCLER_POSITION))
                layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_POSITION));
        } else {
            isSaved = false;
//            getMovies();
        }

        return rootView;
    }

    private void getMovies() {

        info_text.setText("Loading Movies...");

        if (viewPagerPagePosition == 0) {
            Call<MoviesApi> moviesApiCall = new RestClient().getApi_service().getPopular("39dbc8225484ac7c6ceca6ff3701b74b");
            moviesApiCall.enqueue(new Callback<MoviesApi>() {
                @Override
                public void onResponse(@NonNull Call<MoviesApi> call, @NonNull Response<MoviesApi> response) {
                    if (response.body() != null) {
                        MoviesApi moviesApi = response.body();
                        if (moviesApi != null && moviesApi.results != null && moviesApi.results.size() >= 0) {
                            info_text.setVisibility(View.GONE);
                            movies_recycle_view.setVisibility(View.VISIBLE);
                            moviesList = (ArrayList<Movie>) moviesApi.results;
                            movieAdapter.addMovies(moviesApi.results);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MoviesApi> call, @NonNull Throwable t) {
                    info_text.setText("Error Loading Movies");
                }
            });
        }

        if (viewPagerPagePosition == 1) {
            Call<MoviesApi> moviesApiCall = new RestClient().getApi_service().getTopRated("39dbc8225484ac7c6ceca6ff3701b74b");
            moviesApiCall.enqueue(new Callback<MoviesApi>() {
                @Override
                public void onResponse(@NonNull Call<MoviesApi> call, @NonNull Response<MoviesApi> response) {
                    if (response.body() != null) {
                        MoviesApi moviesApi = response.body();
                        if (moviesApi != null && moviesApi.results != null && moviesApi.results.size() >= 0) {
                            info_text.setVisibility(View.GONE);
                            movies_recycle_view.setVisibility(View.VISIBLE);
                            moviesList = (ArrayList<Movie>) moviesApi.results;
                            movieAdapter.addMovies(moviesApi.results);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MoviesApi> call, @NonNull Throwable t) {
                    info_text.setText("Error Loading Movies");
                }
            });
        }

        if (viewPagerPagePosition == 2) {
            task = new AsyncTask<Void, Void, ArrayList<Movie>>() {
                @Override
                protected ArrayList<Movie> doInBackground(Void... voids) {
                    Cursor data = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    return getListFromCursor(data);
                }

                @Override
                protected void onPostExecute(ArrayList<Movie> result) {

                    if (result == null || result.size() == 0) {
                        info_text.setVisibility(View.VISIBLE);
                        movies_recycle_view.setVisibility(View.GONE);
                        info_text.setText("No Favorite Movies Saved");
                        return;
                    }

                    info_text.setVisibility(View.GONE);
                    movies_recycle_view.setVisibility(View.VISIBLE);
                    moviesList = result;
                    movieAdapter.addMovies(result);
                }
            }.execute();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isSaved) getMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null) task.cancel(true);
    }

    private ArrayList<Movie> getListFromCursor(Cursor data) {

        ArrayList<Movie> movies = new ArrayList<>();
        if (data != null) {
            while (data.moveToNext()) {
                int idIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
                int posterIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
                int ratingIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
                int releaseIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                int synopsisIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
                int titleIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
                int backdropIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP);

                int id = data.getInt(idIndex);
                int rating = data.getInt(ratingIndex);
                String title = data.getString(titleIndex);
                String poster = data.getString(posterIndex);
                String releaseDate = data.getString(releaseIndex);
                String overview = data.getString(synopsisIndex);
                String backDrop = data.getString(backdropIndex);

                Movie movie = new Movie();
                movie.id = id;
                movie.title = title;
                movie.posterPath = poster;
                movie.releaseDate = releaseDate;
                movie.overview = overview;
                movie.voteAverage = (double) rating;
                movie.backdropPath = backDrop;
                movies.add(movie);
            }
            data.close();
        }
        return movies;
    }

    @Override
    public void onItemClick(int position) {
        Movie movie = movieAdapter.getItemAtPosition(position);
        if (movie != null) {
            if (movieSelectListener != null)
                movieSelectListener.OnMovieSelected(movie);
        }
    }

}
