package com.example.rasilva.audioserch2.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IAudiosearchService {
    @GET("/api/v1/json/195003/search.php")
    Call<JSONResponse>recuperarDadosArtista(@Query("s") String artist);
}