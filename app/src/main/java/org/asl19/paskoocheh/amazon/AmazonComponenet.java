package org.asl19.paskoocheh.amazon;


import org.asl19.paskoocheh.PaskoochehApplicationModule;
import org.asl19.paskoocheh.rating.RatingDialogFragment;
import org.asl19.paskoocheh.service.PaskoochehConfigSecurityService;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.asl19.paskoocheh.service.PaskoochehConfigVerificationService;
import org.asl19.paskoocheh.service.ToolDownloadSecurityService;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.asl19.paskoocheh.utils.ApkManager;
import org.asl19.paskoocheh.data.source.AmazonRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AmazonModule.class, PaskoochehApplicationModule.class})
public interface AmazonComponenet {

    void inject(PaskoochehConfigService paskoochehConfigService);

    void inject(PaskoochehConfigSecurityService paskoochehConfigSecurityService);

    void inject(PaskoochehConfigVerificationService paskoochehConfigVerificationService);

    void inject(RatingDialogFragment ratingDialogFragment);

    void inject(ToolInfoFragment toolInfoFragment);

    void inject(ToolDownloadService toolDownloadService);

    void inject(ToolDownloadSecurityService toolDownloadSecurityService);

    void inject(ApkManager apkManager);

    void inject(AmazonRepository amazonRepository);
}