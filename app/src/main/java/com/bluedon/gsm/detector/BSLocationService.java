package com.bluedon.gsm.detector;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Author: Keith
 * Date: 2017/8/3
 */

public interface BSLocationService {
    @GET("/bs")
    Call<BSLocations> getBSLocations(
            @Query("oid") String oid, @Query("key") String key,
            @Query("bs") String bs, @Query("output") String output);
}
