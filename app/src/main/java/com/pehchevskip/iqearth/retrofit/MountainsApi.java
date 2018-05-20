package com.pehchevskip.iqearth.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pehchevskip on 19-May-18.
 */

public interface MountainsApi {

    @GET("mountains.json")
    Call<List<String>> getMountains();

}
