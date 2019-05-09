package com.example.android.mymovie.helper;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

    public static final int PAGES_COUNT = 3;
    public static final String POSITION = "position";



    public static final String IMAGE_DOWNLOAD_URL = "https://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185";
    public static final String MOVIES_LIST_INSTANCE_KEY = "movies_list_key";
    public static final String RECYCLER_POSITION = "recycler_states";
    public static final String MOVIE_DETAILS = "recycler_states";
    public static final String MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?api_key=39dbc8225484ac7c6ceca6ff3701b74b";
    public static final String MOVIES_URL1 = "https://api.themoviedb.org/3/movie/top_rated?api_key=39dbc8225484ac7c6ceca6ff3701b74b";
    public static final String BASE_API_URL = "https://api.themoviedb.org/3/";


    public static void getMoviesJson(String url, final OnCallListener listener) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (listener != null)
                        listener.onResponse(null);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (listener != null) {
                        if (response.isSuccessful()) {
                            String result = null;
                            try {
                                result = response.body().string().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            listener.onResponse(result);
                        } else listener.onResponse(null);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Movie> parseMoviesJson(String jsonData){
        return new ArrayList<>();
    }


    public interface OnCallListener {
        void onResponse(String response);
    }

}
