package com.aefyr.sai.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.asl19.paskoocheh.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;

public class Utils {
    private static final String TAG = "SAIUtils";

    @Nullable
    public static String getAppLabel(Context c, String packageName) {
        try {
            PackageManager pm = c.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return pm.getApplicationLabel(appInfo).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String throwableToString(Throwable throwable) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter pw = new PrintWriter(sw);

        throwable.printStackTrace(pw);
        pw.close();

        return sw.toString();
    }

    @SuppressLint("PrivateApi")
    @Nullable
    public static String getSystemProperty(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties")
                    .getDeclaredMethod("get", String.class)
                    .invoke(null, key);
        } catch (Exception e) {
            Log.w("SAIUtils", "Unable to use SystemProperties.get", e);
            return null;
        }
    }

    public static void copyTextToClipboard(Context c, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    public static String getFileNameFromZipEntry(ZipEntry zipEntry) {
        String path = zipEntry.getName();
        int lastIndexOfSeparator = path.lastIndexOf("/");
        if (lastIndexOfSeparator == -1)
            return path;
        return path.substring(lastIndexOfSeparator + 1);
    }

    public static boolean apiIsAtLeast(int sdkInt) {
        return Build.VERSION.SDK_INT >= sdkInt;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static void hideKeyboard(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity != null) {
            hideKeyboard(activity);
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
    }

    public static String escapeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static DecimalFormat sSizeDecimalFormat;

    public static String formatSize(Context c, long bytes) {
        if (sSizeDecimalFormat == null) {
            sSizeDecimalFormat = new DecimalFormat("#.##");
            sSizeDecimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        }

        String[] units = c.getResources().getStringArray(R.array.size_units);

        for (int i = 0; i < units.length; i++) {

            float size = (float) bytes / (float) Math.pow(1024, i);

            if (size < 1024)
                return String.format("%s %s", sSizeDecimalFormat.format(size), units[i]);

        }

        return bytes + " B";
    }

    public static int getThemeColor(Context c, @AttrRes int attribute) {
        TypedValue typedValue = new TypedValue();
        c.getTheme().resolveAttribute(attribute, typedValue, true);
        return typedValue.data;
    }

    private static Handler sMainThreadHandler = new Handler(Looper.getMainLooper());

    public static void onMainThread(Runnable r) {
        sMainThreadHandler.post(r);
    }

    @Nullable
    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1)
            return null;

        return fileName.substring(lastDotIndex + 1);
    }

    @Nullable
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1)
            return null;

        return fileName.substring(0, lastDotIndex);
    }

    public static void softRestartApp(Context c) {
        ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.AppTask task : activityManager.getAppTasks())
            task.finishAndRemoveTask();

        Intent intent = c.getPackageManager().getLaunchIntentForPackage(c.getPackageName());
        c.startActivity(intent);
    }

    public static void hardRestartApp(Context c) {
        ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.AppTask task : activityManager.getAppTasks())
            task.finishAndRemoveTask();

        Intent intent = c.getPackageManager().getLaunchIntentForPackage(c.getPackageName());
        c.startActivity(intent);
        System.exit(0);
    }

    public static int dpToPx(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static int spToPx(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, c.getResources().getDisplayMetrics());
    }

    public static boolean isTv(Context c) {
        UiModeManager uiModeManager = (UiModeManager) c.getSystemService(Context.UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    @Nullable
    public static <T> T getParentAs(Fragment fragment, Class<T> asClass) {
        Object parent = fragment.getParentFragment();
        if (parent == null)
            parent = fragment.getActivity();

        if (asClass.isInstance(parent))
            return asClass.cast(parent);

        return null;
    }

    /**
     * Create a file within {@code dir} directory in app's cache directory. File will have a random name.
     * Even though this method is called createTEMPfile, created file won't be deleted automatically
     *
     * @param context
     * @param dir
     * @param extension
     * @return
     */
    @Nullable
    public static File createTempFileInCache(Context context, String dir, String extension) {
        File directory = new File(context.getCacheDir(), dir);
        if (!directory.exists() && !directory.mkdir())
            return null;

        return createUniqueFileInDirectory(directory, extension);
    }

    @Nullable
    public static File createUniqueFileInDirectory(File dir, String extension) {
        if (!dir.exists() && !dir.mkdirs() && !dir.exists())
            return null;

        if (!dir.canWrite())
            return null;

        File file = null;
        while (file == null || file.exists())
            file = new File(dir, UUID.randomUUID() + "." + extension);

        return file;
    }

    public static File saveImageFromUriAsPng(Context context, Uri imageUri) throws Exception {
        FutureTarget<Bitmap> target = Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .submit();

        try {
            Bitmap bitmap = target.get();

            File tempFile = createTempFileInCache(context, "Utils.saveImageFromUriAsPng", "png");
            if (tempFile == null) {
                throw new IOException("Unable to create file for image");
            }

            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }

            return tempFile;
        } finally {
            Glide.with(context).clear(target);
        }
    }

    public static void saveDrawableAsPng(Drawable drawable, File pngFile) throws Exception {

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);

            try (FileOutputStream outputStream = new FileOutputStream(pngFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }

        } finally {
            if (bitmap != null)
                bitmap.recycle();
            ;
        }

    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
