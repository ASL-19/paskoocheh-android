package org.asl19.paskoocheh.service;

import androidx.annotation.NonNull;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.data.AmazonFormDataRequest;
import org.asl19.paskoocheh.PaskoochehApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static org.asl19.paskoocheh.Constants.AMAZON_API_BASE_URL;
import static org.asl19.paskoocheh.Constants.IV_LENGTH;

import android.util.Log;

/**
 * Retrofit Api Connection.
 */
public class AmazonServiceGenerator {

    private final static String LOG_TAG = AmazonServiceGenerator.class.getCanonicalName();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(AMAZON_API_BASE_URL);

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
        // TODO: This seems to do nothing, remove?
        Interceptor pass = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .build();
                return chain.proceed(request);
            }
        };

        return PaskoochehApplication.getInstance()
            .getOkHttpClientBuilder(pass)
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(150, TimeUnit.SECONDS)
            .build();
    }

    public static AmazonFormDataRequest generateRequest(String json, String uuid) {

        byte[] encrypted = encryptAES(json);
        AmazonFormDataRequest amazonFormDataRequest = new AmazonFormDataRequest();
        amazonFormDataRequest.setKey(String.format(Constants.FILENAME_PREFIX, System.currentTimeMillis(), uuid));
        amazonFormDataRequest.setFile(encrypted);
        return amazonFormDataRequest;
    }

    public static byte[] encryptAES(String content) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.AES_MODE);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Constants.KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(iv);
            outputStream.write(cipher.doFinal(content.getBytes()));

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "error encryption");
            return null;
        }
    }

    private AmazonServiceGenerator() {
    }

}
