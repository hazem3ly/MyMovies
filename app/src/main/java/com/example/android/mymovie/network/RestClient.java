package com.example.android.mymovie.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.mymovie.helper.Utils.BASE_API_URL;

/**
 * Created by Hazem on 12/23/2016.
 */

public class RestClient {
    private Api_Service api_service;

    public RestClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api_service = retrofit.create(Api_Service.class);
    }

    public Api_Service getApi_service() {

        return api_service;

    }

}
