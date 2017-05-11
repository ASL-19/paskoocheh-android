package org.asl19.paskoocheh.data.source;


import android.util.Base64;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.pojo.ActionLog;
import org.asl19.paskoocheh.pojo.DownloadCount;
import org.asl19.paskoocheh.pojo.DownloadCountList;
import org.asl19.paskoocheh.service.PaskoochehApiService;
import org.asl19.paskoocheh.service.ServiceGenerator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.asl19.paskoocheh.Constants.ANDROID_LOGGING_SUFFIX;
import static org.asl19.paskoocheh.Constants.SPACE;

public class DownloadCountRepository implements DownloadCountDataSource {

    @Inject
    DynamoDBMapper dynamoDBMapper;

    public DownloadCountRepository() {

    }

    @Override
    public void getDownloadCountList(final GetDownloadListCallback callback) {
        PaskoochehApiService paskoochehApiService = ServiceGenerator.createService(PaskoochehApiService.class);

        Call<DownloadCountList> call = paskoochehApiService.getDownloadCountList();
        call.enqueue(new Callback<DownloadCountList>() {
            @Override
            public void onResponse(Call<DownloadCountList> call, Response<DownloadCountList> response) {
                switch (response.code()) {
                    case HTTP_OK:
                        callback.onGetDownloadsSuccessful(response.body());
                        break;
                    default:
                        callback.onGetDownloadsFailed();
                        break;
                }
            }

            @Override
            public void onFailure(Call<DownloadCountList> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onGetDownloadsFailed();
            }
        });
    }

    @Override
    public void getDownloadCount(final String toolName, final GetDownloadCountCallback callback) {
        PaskoochehApiService paskoochehApiService = ServiceGenerator.createService(PaskoochehApiService.class);

        Call<DownloadCountList> call = paskoochehApiService.getDownloadCountList();
        call.enqueue(new Callback<DownloadCountList>() {
            @Override
            public void onResponse(Call<DownloadCountList> call, Response<DownloadCountList> response) {
                switch (response.code()) {
                    case HTTP_OK:
                        for (DownloadCount downloadCount: response.body().getApps()) {
                            if (downloadCount.getAppName().equals(toolName)) {
                                callback.onGetDownlaodCountSuccessful(downloadCount.getDownloadCount().toString());
                                return;
                            }
                        }
                        callback.onGetDownloadCountFailed();
                        break;
                    default:
                        callback.onGetDownloadCountFailed();
                        break;
                }
            }

            @Override
            public void onFailure(Call<DownloadCountList> call, Throwable throwable) {
                Log.e(getClass().getName(), throwable.toString());
                callback.onGetDownloadCountFailed();
            }
        });
    }

    @Override
    public void registerDownload(final String uuid, final String tool, final DynamoDBMapper dynamoDBMapper, final RegisterDownloadCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                MessageDigest md;
                try {
                    ActionLog actionLog = new ActionLog();
                    actionLog.setActionTime((double) System.currentTimeMillis() / 1000L);
                    actionLog.setActionName(tool.replaceAll(SPACE, "").toLowerCase() + ANDROID_LOGGING_SUFFIX);
                    actionLog.setDownloadSource(Constants.ANDROID_APP);

                    md = MessageDigest.getInstance("SHA-512");
                    md.update(uuid.getBytes("UTF-8"));
                    byte[] digest = md.digest();
                    actionLog.setUsername(Base64.encodeToString(digest, Base64.NO_WRAP));

                    dynamoDBMapper.save(actionLog);
                    callback.onRegisterDownloadSuccessful();
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException exception) {
                    Log.e(getClass().getName(), exception.toString());
                    callback.onRegisterDownloadFailed();
                }
            }
        }).start();
    }
}
