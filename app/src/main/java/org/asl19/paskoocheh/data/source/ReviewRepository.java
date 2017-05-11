package org.asl19.paskoocheh.data.source;


import android.util.Log;

import org.asl19.paskoocheh.data.GetReviewRequest;
import org.asl19.paskoocheh.data.SendReviewRequest;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.pojo.ReviewList;
import org.asl19.paskoocheh.service.PaskoochehApiService;
import org.asl19.paskoocheh.service.ServiceGenerator;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class ReviewRepository implements ReviewDataSource {

    public ReviewRepository() {
    }

    @Override
    public void getReviewList(GetReviewRequest getReviewRequest, final GetReviewListCallback callback) {
        PaskoochehApiService paskoochehApiService = ServiceGenerator.createService(PaskoochehApiService.class);

        Call<ReviewList> call = paskoochehApiService.getToolReviewList(getReviewRequest);
        call.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                switch (response.code()) {
                    case HTTP_OK:
                        ArrayList<Review> filteredReviews = new ArrayList<>();
                        ReviewList reviewList = response.body();
                        for (Review review: reviewList.getAllReviews()) {
                            if (!review.getText().isEmpty() || !review.getTitle().isEmpty()) {
                                filteredReviews.add(review);
                            }
                        }
                        reviewList.setAllReviews(filteredReviews);
                        reviewList.setCount(filteredReviews.size());
                        callback.onGetReviewsSuccessful(reviewList);
                        break;
                    default:
                        callback.onGetReviewsFailed();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onGetReviewsFailed();
            }
        });
    }

    @Override
    public void submitReview(SendReviewRequest sendReviewRequest, final SendReviewCallback callback) {
        PaskoochehApiService paskoochehApiService = ServiceGenerator.createService(PaskoochehApiService.class);

        Call<ResponseBody> call = paskoochehApiService.sendReview(sendReviewRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case HTTP_OK:
                        callback.onSendReviewSuccessful();
                        break;
                    default:
                        callback.onSendReviewFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onSendReviewFailed();
            }
        });
    }
}
