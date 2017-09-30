package com.example.android.mymovie.helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HazemAli on 9/29/2017.
 */

public class MovieVideos {

    @SerializedName("results")
    @Expose
    public List<Videos> results = null;

    public class Videos {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("key")
        @Expose
        public String key;
        @SerializedName("name")
        @Expose
        public String name;

    }


}
