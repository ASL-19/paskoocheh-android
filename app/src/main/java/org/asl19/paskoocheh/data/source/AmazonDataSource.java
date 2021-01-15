package org.asl19.paskoocheh.data.source;


import org.asl19.paskoocheh.data.AmazonContentBodyRequest;

public interface AmazonDataSource {

    interface SubmitRequestCallback {

        void onSubmitRequestSuccessful();

        void onSubmitRequestFailed();
    }

    void onSubmitRequest(AmazonContentBodyRequest request, SubmitRequestCallback callback);
}
