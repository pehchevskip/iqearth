package com.pehchevskip.iqearth.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pehchevskip on 18-May-18.
 */

public interface AnimalsApi {

    @GET("words.json")
    Call<List<String>> getAnimals();

}
