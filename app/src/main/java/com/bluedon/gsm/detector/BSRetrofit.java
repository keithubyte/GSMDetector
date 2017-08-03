package com.bluedon.gsm.detector;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: Administrator
 * Date: 2017/8/3
 */

public class BSRetrofit {
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.gpsspg.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final BSLocationService service = retrofit.create(BSLocationService.class);

    public static void getBSLocations(List<GSMCellInfo> cells) {
        String bs = getBS(cells);
        Call<BSLocations> call = service.getBSLocations("5526", "06FB08343C125023BF1B3928D003F9F7", bs, "json");
        call.enqueue(new Callback<BSLocations>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(@NonNull Call<BSLocations> call, @NonNull Response<BSLocations> response) {
                if (response.body() != null) {
                    List<BSLocation> locations = response.body().result;
                    for (BSLocation location : locations) {
                        Log.w("BS", "location " + location.roads);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BSLocations> call, @NonNull Throwable t) {
                Log.e("BS", "fail");
            }
        });
    }

    private static String getBS(List<GSMCellInfo> cells) {
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
