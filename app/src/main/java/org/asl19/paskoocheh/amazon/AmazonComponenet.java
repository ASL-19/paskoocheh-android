package org.asl19.paskoocheh.amazon;


import org.asl19.paskoocheh.PaskoochehApplicationModule;
import org.asl19.paskoocheh.baseactivities.BaseNavigationActivity;
import org.asl19.paskoocheh.installedtoollist.InstalledToolListFragment;
import org.asl19.paskoocheh.rating.RatingDialogFragment;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.toollist.ToolListFragment;
import org.asl19.paskoocheh.update.UpdateDialogFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AmazonModule.class, PaskoochehApplicationModule.class})
public interface AmazonComponenet {

    void inject(PaskoochehConfigService paskoochehConfigService);

    void inject(ToolListFragment toolListFragment);

    void inject(RatingDialogFragment ratingDialogFragment);

    void inject(ToolInfoFragment toolInfoFragment);

    void inject(InstalledToolListFragment installedToolListFragment);

    void inject(ToolDownloadService toolDownloadService);

    void inject(BaseNavigationActivity baseNavigationActivity);

    void inject(UpdateDialogFragment updateDialogFragment);
}