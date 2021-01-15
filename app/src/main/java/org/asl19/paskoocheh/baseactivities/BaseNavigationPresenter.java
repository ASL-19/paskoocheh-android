package org.asl19.paskoocheh.baseactivities;


import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

import lombok.NonNull;

public class BaseNavigationPresenter implements BaseNavigationContract.Presenter {

    private final BaseNavigationContract.NavigationView navigationView;
    private final VersionDataSource versionRepository;

    public BaseNavigationPresenter(@NonNull BaseNavigationContract.NavigationView navigationView, @NonNull VersionDataSource versionRepository) {
        this.navigationView = navigationView;
        this.versionRepository = versionRepository;

        this.navigationView.setPresenter(this);
    }

    @Override
    public void getInstalledVersions() {
        versionRepository.getInstalledVersions(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (navigationView.isActive()) {
                    navigationView.getInstalledVersionsSuccessful(versions);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (navigationView.isActive()) {
                    navigationView.getInstalledVersionsFailed();
                }
            }
        });
    }

    @Override
    public void getAndroidVersions() {
        versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (navigationView.isActive()) {
                    navigationView.getVersionsSuccessful(versions);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (navigationView.isActive()) {
                    navigationView.getVersionsFailed();
                }
            }
        });
    }
}
