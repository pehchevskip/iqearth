package com.pehchevskip.iqearth.retrofit;

import com.pehchevskip.iqearth.model.api.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pehchevskip on 18-May-18.
 */

public interface CountryApi {

    @GET("all?fields=name;capital")
    Call<List<Country>> getCountries();

}

