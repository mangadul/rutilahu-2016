package com.alphamedia.rutilahu.api;

import com.alphamedia.rutilahu.Config;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface ApiService {

    public static final String BASE_URL = Config.BASE_URL;

    @Multipart
    @POST(Config.POST_UPLOAD)
    //ApiResult
    String uploadFile(@Part("userfile") TypedFile resource, @Query("path") String path);
}