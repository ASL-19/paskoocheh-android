package org.asl19.paskoocheh.amazon;


import org.asl19.paskoocheh.PaskoochehApplicationModule;
import org.asl19.paskoocheh.data.source.DownloadCountRepository;
import org.asl19.paskoocheh.rating.RatingDialogFragment;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AmazonModule.class, PaskoochehApplicationModule.class})
public interface AmazonComponenet {

    void inject(PaskoochehConfigService paskoochehConfigService);

    void inject(RatingDialogFragment ratingDialogFragment);

    void inject(ToolInfoFragment toolInfoFragment);

    void inject(ToolDownloadService toolDownloadService);

    void inject(DownloadCountRepository downloadCountRepository);
}