package org.asl19.paskoocheh.service;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static org.asl19.paskoocheh.Constants.API;
import static org.asl19.paskoocheh.Constants.URL;

/**
 * Retrofit Api Connection.
 */
public class ServiceGenerator {

    private static Retrofit.Builder builder =
        new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(URL);

    public static <S> S createService(Class<S> serviceClass) {
        OkHttpClient client = getOkHttpClient();

        Retrofit retrofit =
            builder.client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    /**
     * Get OkHttpClient.
     *
     * @return OkHttpClient.
     */
    @NonNull
    public static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("x-api-key", API)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .build();

                    return chain.proceed(request);
                }
        });

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpClient.addInterceptor(loggingInterceptor).build();
    }


    private ServiceGenerator() {
    }

}
