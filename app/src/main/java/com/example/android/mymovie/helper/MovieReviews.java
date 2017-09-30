package com.example.android.mymovie.helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviews {

    @SerializedName("results")
    @Expose
    public List<Reviews> reviewsList = null;

    public class Reviews {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("content")
        @Expose
        public String content;
        @SerializedName("url")
        @Expose
        public String url;

    }


}
