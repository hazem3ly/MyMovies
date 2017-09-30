package com.example.android.mymovie.network;

import com.example.android.mymovie.helper.MovieReviews;
import com.example.android.mymovie.helper.MovieVideos;
import com.example.android.mymovie.helper.MoviesApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api_Service {

    @GET("movie/top_rated")
    Call<MoviesApi> getTopRated(@Query("api_key") String key);

    @GET("movie/popular")
    Call<MoviesApi> getPopular(@Query("api_key") String key);

    @GET("movie/{id}/videos")
    Call<MovieVideos> getVideos(@Path("id") String movieId, @Query("api_key") String key);

    @GET("movie/{id}/reviews ")
    Call<MovieReviews> getReviews(@Path("id") String movieId, @Query("api_key") String key);


}




