package org.asl19.paskoocheh.data.source;


import android.util.Log;

import org.asl19.paskoocheh.pojo.Rating;
import org.asl19.paskoocheh.pojo.RatingList;
import org.asl19.paskoocheh.service.PaskoochehApiService;
import org.asl19.paskoocheh.service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class RatingRepository implements RatingDataSource {
    @Override
    public void getRatingList(final GetRatingListCallback callback) {
        PaskoochehApiService paskoochehApiService = ServiceGenerator.createService(PaskoochehApiService.class);

        Call<RatingList> call = paskoochehApiService.getRatingList();
        call.enqueue(new Callback<RatingList>() {
            @Override
            public void onResponse(Call<RatingList> call, Response<RatingList> response) {
                switch (response.code()) {
                    case HTTP_OK:
                        callback.onGetRatingSuccessful(response.body());
                        break;
                    default:
                        callback.onGetRatingsFailed();
                        break;
                }
            }

            @Override
            public void onFailure(Call<RatingList> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onGetRatingsFailed();
            }
        });
    }

    @Override
    public void getRating(final String toolName, final GetRatingCallback callback) {
        PaskoochehApiService paskoochehApiService = ServiceGenerator.createService(PaskoochehApiService.class);

        Call<RatingList> call = paskoochehApiService.getRatingList();
        call.enqueue(new Callback<RatingList>() {
            @Override
            public void onResponse(Call<RatingList> call, Response<RatingList> response) {
                switch (response.code()) {
                    case HTTP_OK:
                        for (Rating rating: response.body().getItems()) {
                            if (rating.getAppName().equals(toolName)) {
                                callback.onGetRatingSuccessful(rating.getRating());
                                return;
                            }
                        }
                        callback.onGetRatingFailed();
                        break;
                    default:
                        callback.onGetRatingFailed();
                        break;
                }
            }

            @Override
            public void onFailure(Call<RatingList> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onGetRatingFailed();
            }
        });
    }
}
