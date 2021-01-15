package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class ToolLocalDataSource implements ToolDataSource {

    private static volatile ToolLocalDataSource INSTANCE;

    private ToolDao toolDao;

    private AppExecutors appExecutors;

    private ToolLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull ToolDao toolDao) {
        this.appExecutors = appExecutors;
        this.toolDao = toolDao;
    }

    public static ToolLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull ToolDao toolDao) {
        if (INSTANCE == null) {
            synchronized (ToolLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ToolLocalDataSource(appExecutors, toolDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getTools(final ToolDataSource.GetToolListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Tool> tools = toolDao.getAllTools();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tools == null) {
                            callback.onGetToolListFailed();
                        } else {
                            callback.onGetToolListSuccessful(tools);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void getTool(final long toolId, final ToolDataSource.GetToolCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Tool tool = toolDao.getTool(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tool == null) {
                            callback.onGetToolFailed();
                        } else {
                            callback.onGetToolSuccessful(tool);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveTool(@NonNull final Tool... tool) {
        checkNotNull(tool);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                toolDao.insertAll(tool);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                toolDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
