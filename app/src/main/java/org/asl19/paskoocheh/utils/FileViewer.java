package org.asl19.paskoocheh.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.pojo.Version;

import java.io.File;

public class FileViewer {

    public static void viewFile(Context context, File file, String fileExtension) {
        Uri internalUri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

        Intent intentViewFile = new Intent(Intent.ACTION_VIEW);
        intentViewFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentViewFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (fileExtension.equals("html") || fileExtension.equals("htm")) {
            intentViewFile.setData(internalUri);
        } else {
            intentViewFile.setDataAndType(internalUri, "application/" + fileExtension);
        }

        context.startActivity(intentViewFile);
    }

    public static String getFileExtension(Version version) {
        if (version == null) {
            return null;
        }

        Integer urlSplit = version.getS3Key().lastIndexOf("/");
        String externalFile = version.getS3Key().substring(urlSplit + 1);
        String[] tmp = externalFile.split("\\.");
        String fileExtension = tmp[tmp.length-1];

        return fileExtension;
    }
}
