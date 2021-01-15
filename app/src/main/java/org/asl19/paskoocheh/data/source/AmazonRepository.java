package org.asl19.paskoocheh.data.source;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.asl19.paskoocheh.data.AmazonContentBodyRequest;
import org.asl19.paskoocheh.data.AmazonFormDataRequest;
import org.asl19.paskoocheh.data.AmazonFormDataRequestSerializer;
import org.asl19.paskoocheh.service.AmazonServiceGenerator;
import org.asl19.paskoocheh.service.PaskoochehApiService;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;

public class AmazonRepository implements AmazonDataSource {
    private Context context;

    public AmazonRepository(Context context) {
        this.context = context;
    }

    @Override
    public void onSubmitRequest(AmazonContentBodyRequest request, final SubmitRequestCallback callback) {

        Gson gson = new GsonBuilder().registerTypeAdapter(AmazonFormDataRequest.class, new AmazonFormDataRequestSerializer()).create();
        String feedbackString = gson.toJson(request);

        String uuid = context.getSharedPreferences(
                PASKOOCHEH_PREFS,
                Context.MODE_PRIVATE
        ).getString(PASKOOCHEH_UUID, "");

        AmazonFormDataRequest amazonRequest = AmazonServiceGenerator.generateRequest(feedbackString, uuid);

        PaskoochehApiService paskoochehApiService = AmazonServiceGenerator.createService(PaskoochehApiService.class);
        Call<ResponseBody> call = paskoochehApiService.submitAmazonRequest(
                RequestBody.create(null, amazonRequest.getAcl()),
                RequestBody.create(null, amazonRequest.getKey()),
                RequestBody.create(null, amazonRequest.getPolicy()),
                RequestBody.create(null, amazonRequest.getXAmzAlgorithm()),
                RequestBody.create(null, amazonRequest.getXAmzCredential()),
                RequestBody.create(null, amazonRequest.getXAmzDate()),
                RequestBody.create(null, amazonRequest.getXAmzSignature()),
                RequestBody.create(null, amazonRequest.getFile())
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case HTTP_NO_CONTENT:
                        callback.onSubmitRequestSuccessful();
                        break;
                    default:
                        callback.onSubmitRequestFailed();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onSubmitRequestFailed();
            }
        });
    }
}
