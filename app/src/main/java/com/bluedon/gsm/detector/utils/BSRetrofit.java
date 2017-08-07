package com.bluedon.gsm.detector.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bluedon.gsm.detector.data.BSInfo;
import com.bluedon.gsm.detector.data.BSLocation;
import com.bluedon.gsm.detector.data.BSResponse;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: Keith
 * Date: 2017/8/3
 */

public class BSRetrofit {
    private static final String TAG = BSRetrofit.class.getSimpleName();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.gpsspg.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder().addInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
            .build();
    private static final BSLocationService service = retrofit.create(BSLocationService.class);
    private static final String API_OID = "5526";
    private static final String API_KEY = "06FB08343C125023BF1B3928D003F9F7";
    private static final String API_OUTPUT = "json";

    public static void queryBSLocations(List<BSInfo> cells, @NonNull final BSResponseCallBack callback) {
        String bs = getBS(cells);
        Call<BSResponse> call = service.getBSLocations(API_OID, API_KEY, bs, API_OUTPUT);
        Log.e(TAG, call.request().url().toString());
        call.enqueue(new Callback<BSResponse>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(@NonNull Call<BSResponse> call, @NonNull Response<BSResponse> response) {
                if (response.body() != null) {
                    List<BSLocation> locations = response.body().result;
                    for (BSLocation location : locations) {
                        Log.w(TAG, "location " + location.roads);
                    }
                    callback.onSuccess(locations);
                }
                callback.onFailure(new Throwable("response body is null!"));
            }

            @Override
            public void onFailure(@NonNull Call<BSResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "fail by " + t.getMessage());
                callback.onFailure(t);
            }
        });
    }

    private static String getBS(List<BSInfo> cells) {
        StringBuilder bs = new StringBuilder();
        int size = cells.size();
        for (int i = 0; i < size; i++) {
            bs.append(cells.get(i).getBSInfo());
            if (i < size - 1) {
                bs.append("|");
            }
        }
        return bs.toString();
    }
}
