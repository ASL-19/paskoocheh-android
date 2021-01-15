package org.asl19.paskoocheh.service;

import androidx.annotation.NonNull;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.data.AmazonFormDataRequest;

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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static org.asl19.paskoocheh.Constants.AMAZON_URL;
import static org.asl19.paskoocheh.Constants.IV_LENGTH;

/**
 * Retrofit Api Connection.
 */
public class AmazonServiceGenerator {

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(AMAZON_URL);

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
                        .build();
                return chain.proceed(request);
            }
        });

        httpClient.connectTimeout(150, TimeUnit.SECONDS);
        httpClient.readTimeout(150, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpClient.addInterceptor(loggingInterceptor).build();
    }

    public static AmazonFormDataRequest generateRequest(String json, String uuid) {

        byte[] encrypted = encryptAES(json);
        AmazonFormDataRequest amazonFormDataRequest = new AmazonFormDataRequest();
        amazonFormDataRequest.setKey(String.format(Constants.FILENAME_PREFIX, System.currentTimeMillis(), uuid));
        amazonFormDataRequest.setFile(encrypted);
        return amazonFormDataRequest;
    }

    private static byte[] encryptAES(String content) {
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
            return null;
        }
    }

    private AmazonServiceGenerator() {
    }

}
