package org.asl19.paskoocheh.data.source;


import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.UploadOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.amazon.S3Clients;
import org.asl19.paskoocheh.data.AmazonContentBodyRequest;
import org.asl19.paskoocheh.data.AmazonFormDataRequest;
import org.asl19.paskoocheh.data.AmazonFormDataRequestSerializer;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.service.AmazonServiceGenerator;
import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Inject;

public class AmazonRepository implements AmazonDataSource {
    private static String LOG_TAG = AmazonRepository.class.getCanonicalName();
    private Context context;
    @Inject
    S3Clients s3Clients;

    public AmazonRepository(Context context) {
        PaskoochehApplication.getInstance().getAmazonComponenet().inject(this);
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

        final TransferUtility transferUtility = s3Clients.chooseTransferUtility();

        UploadOptions.Builder builder = UploadOptions.builder();
        builder.bucket(Constants.AMAZON_API_ENDPOINT);
        builder.transferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    callback.onSubmitRequestSuccessful();
                /*
                    TODO this code is just to troubleshoot issue with the encryption of file
                    ByteArrayInputStream in = new ByteArrayInputStream(amazonRequest.getFile());
                    int n = in.available();
                    byte[] bytes = new byte[n];
                    in.read(bytes, 0, n);
                    String s = new String(bytes, StandardCharsets.UTF_8); // Or any encoding.
                    Log.d(LOG_TAG, "onStateChanged is COMPLETED. key=" +  amazonRequest.getKey() + "\n file-content = " + s);
                */
                    Log.d(LOG_TAG, "onStateChanged is COMPLETED success upload.");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                EventBus.getDefault().post(new Event.Timeout());
                FirebaseCrashlytics.getInstance().recordException(ex);
                Log.e(LOG_TAG, ex.getMessage().toString());
                Log.e(LOG_TAG, ex.getStackTrace().toString());
                Log.d(LOG_TAG, "fail");
                callback.onSubmitRequestFailed();
            }
        });

        UploadOptions uploadOptions = new UploadOptions(builder);

        try {
            TransferObserver observer = transferUtility.upload(amazonRequest.getKey(), new ByteArrayInputStream(amazonRequest.getFile()), uploadOptions);
        } catch (IOException ex) {
            Log.e(LOG_TAG, ex.getStackTrace().toString());
        }
/*
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
        */
    }
}
