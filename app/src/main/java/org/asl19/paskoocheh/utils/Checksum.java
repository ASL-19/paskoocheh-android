package org.asl19.paskoocheh.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class operates on exclusively static methods.
 * Generating checksum of files and comparing to a given checksum.
 */
public final class Checksum {
    private static final String TAG = "Checksum";

    private Checksum() {

    }

    /**
     * Determines if the given checksum matches with the calculated checksum from
     * the given file.
     *
     * @param sha256 A given checksum.
     * @param updateFile A file for which a checksum needs to be calculated
     * @return true iff the given sha256 checksum is identical to the updateFile calculated checksum.
     */
    public static boolean checkChecksum(String sha256, File updateFile) {
        if (TextUtils.isEmpty(sha256) || updateFile == null) {
            Log.e(TAG, "Checksum string empty or updateFile null");

            return false;
        }

        String calculatedDigest = calculateChecksum(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");

            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + sha256);

        return calculatedDigest.equalsIgnoreCase(sha256);
    }

    /**
     * Generate a checksum from the given file.
     *
     * @param updateFile A checksum for which a checksum needs to be calculated.
     * @return The calculated checksum.
     */
    public static String calculateChecksum(File updateFile) {
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA256");
            FileInputStream fis = new FileInputStream(updateFile);

            byte[] data = new byte[1024];
            int read;
            while ((read = fis.read(data)) != -1) {
                sha256.update(data, 0, read);
            }

            byte[] hashBytes = sha256.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hashBytes.length; i++) {
                sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException exception) {
            Log.e(TAG, exception.toString());
        }
        return null;
    }
}