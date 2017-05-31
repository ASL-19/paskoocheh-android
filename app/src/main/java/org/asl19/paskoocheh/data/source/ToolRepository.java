package org.asl19.paskoocheh.data.source;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.fernandocejas.arrow.optional.Optional;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.loading.LoadingActivity;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.proto.Config;
import org.asl19.paskoocheh.proto.Platform;
import org.asl19.paskoocheh.proto.PlatformName;
import org.asl19.paskoocheh.proto.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.asl19.paskoocheh.Constants.CONFIG_FILE;

public class ToolRepository implements ToolDataSource {

    private Context context;
    private PackageManager packageManager;

    public ToolRepository(Context context, PackageManager packageManager) {
        this.context = context;
        this.packageManager = packageManager;
    }

    @Override
    public void getAndroidTools(GetToolsCallback callback) {
        Optional<List<Tool>> optional = getTools();
        if (optional.isPresent()) {
            List<AndroidTool> tools = new ArrayList<>();
            for (Tool tool: optional.get()) {
                tools.add(new AndroidTool(tool, "fa"));
            }
            callback.onGetToolsSuccessful(tools);
        } else {
            callback.onGetToolsFailed();
        }
    }

    @Override
    public void getInstalledTools(GetToolsCallback callback) {
        Optional<List<Tool>> optional = getTools();
        if (optional.isPresent()) {
            List<AndroidTool> tools = new ArrayList<>();
            for (Tool tool: optional.get()) {
                AndroidTool androidTool = new AndroidTool(tool, "fa");
                try {
                    int installedVersionCode = packageManager.getPackageInfo(androidTool.getPackageName(), 0).versionCode;
                    androidTool.setInstalled(true);
                    if (androidTool.getVersionCode() > installedVersionCode) {
                        androidTool.setUpdateAvailable(true);
                    }
                    tools.add(androidTool);
                } catch (PackageManager.NameNotFoundException ignored) {
                    androidTool.setUpdateAvailable(false);
                    androidTool.setInstalled(false);
                }
            }
            callback.onGetToolsSuccessful(tools);
        } else {
            callback.onGetToolsFailed();
        }
    }

    @Override
    public void getFeaturedTools(GetToolsCallback callback) {
        Optional<List<Tool>> optional = getTools();
        if (optional.isPresent()) {
            List<AndroidTool> featuredList = new ArrayList<>();
            for (Tool tool: optional.get()) {
                if (tool.is_featured) {
                    featuredList.add(new AndroidTool(tool, "fa"));
                }
            }
            callback.onGetToolsSuccessful(featuredList);
        } else {
            callback.onGetToolsFailed();
        }
    }

    @Override
    public void getTool(long toolId, GetToolCallback callback) {
        Optional<List<Tool>> optional = getTools();
        if (optional.isPresent()) {
            for (Tool tool: optional.get()) {
                if (tool.tool_id == toolId) {
                    callback.onGetToolSuccessful(new AndroidTool(tool, "fa"));
                    return;
                }
            }
            callback.onGetToolFailed();
        } else {
            callback.onGetToolFailed();
        }
    }

    private Optional<List<Tool>> getTools() {
        File proto = new File(context.getApplicationContext().getFilesDir() + "/" + CONFIG_FILE);
        try {
            InputStream inputStream = new FileInputStream(proto);
            Config config = Config.ADAPTER.decode(inputStream);
            for (Platform platform : config.platforms) {
                if (platform.name.equals(PlatformName.ANDROID)) {
                    return Optional.of(platform.tools);
                }
            }
            return Optional.absent();
        } catch (Exception e) {
            Crashlytics.logException(e);
            Toast.makeText(context, R.string.reloading_content, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoadingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            Log.e("Exception", e.toString());
            return Optional.absent();
        }
    }
}
