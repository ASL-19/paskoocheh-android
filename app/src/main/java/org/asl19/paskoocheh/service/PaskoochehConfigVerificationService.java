package org.asl19.paskoocheh.service;


import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.source.LastModifiedDataSource;
import org.asl19.paskoocheh.data.source.Local.DownloadAndRatingLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.FaqLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.GuideLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.ImagesLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.LastModifiedLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.LocalizedInfoLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.NameLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.PaskoochehDatabase;
import org.asl19.paskoocheh.data.source.Local.ReviewLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.TextLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.ToolLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.TutorialLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.pojo.Category;
import org.asl19.paskoocheh.pojo.Config;
import org.asl19.paskoocheh.pojo.ConfigFaq;
import org.asl19.paskoocheh.pojo.ConfigGuideTutorial;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Faq;
import org.asl19.paskoocheh.pojo.Faqs;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.pojo.GuideTutorial;
import org.asl19.paskoocheh.pojo.LastModified;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.pojo.Text;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Tutorial;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.utils.AppExecutors;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.inject.Inject;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.Constants.ASC;
import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.CONFIG_DIRECTORY;
import static org.asl19.paskoocheh.Constants.DOWNLOADS_AND_RATINGS;
import static org.asl19.paskoocheh.Constants.EN;
import static org.asl19.paskoocheh.Constants.FA;
import static org.asl19.paskoocheh.Constants.FAQS;
import static org.asl19.paskoocheh.Constants.GUIDES_AND_TUTORIALS;
import static org.asl19.paskoocheh.Constants.REVIEWS;
import static org.asl19.paskoocheh.Constants.TEXTS;
import static org.asl19.paskoocheh.utils.PGPUtil.verifySignature;

public class PaskoochehConfigVerificationService extends IntentService {

    public static final String CONFIG = "CONFIG";

    @Inject
    AmazonS3Client amazonS3Client;

    public PaskoochehConfigVerificationService() {
        super("PaskoochehConfigVerificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((PaskoochehApplication) getApplication()).getAmazonComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String originalFilename = intent.getExtras().getString(CONFIG);
            String securityFilename = originalFilename + ASC;
            Long amazonLastModifiedTime = amazonS3Client.getObjectMetadata(BUCKET_NAME + CONFIG_DIRECTORY, originalFilename).getLastModified().getTime();

            final File securityFile = new File(getApplicationContext().getFilesDir() + "/" + securityFilename);
            final File originalFile = new File(getApplicationContext().getFilesDir() + "/" + originalFilename);

            if (originalFile.exists() &&
                    securityFile.exists() &&
                    verifySignature(
                    new BufferedInputStream(new FileInputStream(originalFile)),
                    new BufferedInputStream(new FileInputStream(securityFile)),
                    new BufferedInputStream(getApplicationContext().getAssets().open("EA6173BA.pub")))) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                switch (originalFilename) {
                    case APPS:
                        loadAppsJson(gson, originalFile);
                        EventBus.getDefault().post(new Event.PaskoochehConfigComplete());
                        break;
                    case DOWNLOADS_AND_RATINGS:
                        loadDownloadsAndRating(gson, originalFile);
                        break;
                    case FAQS:
                        loadFaqs(gson, originalFile);
                        break;
                    case GUIDES_AND_TUTORIALS:
                        loadGuidesAndTutorials(gson, originalFile);
                        break;
                    case REVIEWS:
                        loadReviews(gson, originalFile);
                        break;
                    case TEXTS:
                        loadTexts(gson, originalFile);
                        break;
                    default:
                        Log.e(PaskoochehConfigSecurityService.class.getSimpleName(), "UNKNOWN FILE");
                }

                PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());
                LastModifiedDataSource lastModifiedLocalDataSource = LastModifiedLocalDataSource.getInstance(new AppExecutors(), database.lastModifiedDao());

                LastModified lastModified = new LastModified();
                lastModified.setConfigFile(originalFilename);
                lastModified.setLastModified(amazonLastModifiedTime);

                lastModifiedLocalDataSource.saveLastModified(lastModified);
            } else {
                Handler mainHandler = new Handler(Looper.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {EventBus.getDefault().post(new Event.Timeout());}
                };
                mainHandler.post(myRunnable);
                return;
            }

        } catch (Exception ex) {
            EventBus.getDefault().post(new Event.Timeout());

            Toast.makeText(
                    PaskoochehConfigVerificationService.this,
                    getString(R.string.download_failed_retry),
                    Toast.LENGTH_SHORT
            ).show();

            Crashlytics.logException(ex);

            Log.e("SecurityConfigService", ex.toString());
        }
    }

    private void loadTexts(Gson gson, File file) throws FileNotFoundException {
        Text[] texts = gson.fromJson(new FileReader(file), Text[].class);
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        TextLocalDataSource textLocalDataSource = TextLocalDataSource.getInstance(new AppExecutors(), database.textDao());

        textLocalDataSource.clearTable();

        textLocalDataSource.saveTexts(texts);
    }

    private void loadReviews(Gson gson, File file) throws FileNotFoundException {
        Review[] reviewses = gson.fromJson(new FileReader(file), Review[].class);
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        ReviewLocalDataSource reviewLocalDataSource = ReviewLocalDataSource.getInstance(new AppExecutors(), database.reviewDao());

        reviewLocalDataSource.clearTable();

        reviewLocalDataSource.saveReview(reviewses);
    }

    private void loadGuidesAndTutorials(Gson gson, File file) throws FileNotFoundException{
        ConfigGuideTutorial configGuideTutorial = gson.fromJson(new FileReader(file), ConfigGuideTutorial.class);
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        GuideLocalDataSource guideLocalDataSource = GuideLocalDataSource.getInstance(new AppExecutors(), database.guideDao());
        TutorialLocalDataSource tutorialLocalDataSource = TutorialLocalDataSource.getInstance(new AppExecutors(), database.tutorialDao());

        guideLocalDataSource.clearTable();
        tutorialLocalDataSource.clearTable();

        for (GuideTutorial guidesTutorials : configGuideTutorial.getVersions().getAndroid()) {
            if (guidesTutorials.getGuide() != null) {
                for (Guide guide : guidesTutorials.getGuide()) {
                    guide.setToolId(guidesTutorials.getToolId());
                }
                guideLocalDataSource.saveGuides(guidesTutorials.getGuide());
            }

            Tutorial[] tutorials = guidesTutorials.getTutorial();
            if (tutorials != null) {
                for (Tutorial tutorial: tutorials) {
                    tutorial.setToolId(guidesTutorials.getToolId());
                }
                tutorialLocalDataSource.saveTutorial(tutorials);
            }
        }
    }

    private void loadFaqs(Gson gson, File file) throws FileNotFoundException {
        ConfigFaq configFaq = gson.fromJson(new FileReader(file), ConfigFaq.class);
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        FaqLocalDataSource faqLocalDataSource = FaqLocalDataSource.getInstance(new AppExecutors(), database.faqDao());

        faqLocalDataSource.clearTable();

        for (Faqs faqs: configFaq.getVersions().getAndroid()) {
            if (faqs.getFaq() != null) {
                for (Faq faq : faqs.getFaq()) {
                    faq.setToolId(faqs.getToolId());
                    faqLocalDataSource.saveFaqs(faq);
                }
            }
        }
    }

    private void loadDownloadsAndRating(Gson gson, File file) throws FileNotFoundException {
        DownloadAndRating[] downloadsAndRatings = gson.fromJson(new FileReader(file), DownloadAndRating[].class);
        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        DownloadAndRatingLocalDataSource downloadAndRatingLocalDataSource = DownloadAndRatingLocalDataSource.getInstance(new AppExecutors(), database.downloadAndRatingDao());

        downloadAndRatingLocalDataSource.clearTable();

        downloadAndRatingLocalDataSource.saveDownloadAndRating(downloadsAndRatings);
    }

    private void loadAppsJson(Gson gson, File file) throws FileNotFoundException {
        AppExecutors appExecutors = new AppExecutors();
        Config config = gson.fromJson(new FileReader(file), Config.class);

        PaskoochehDatabase database = PaskoochehDatabase.getInstance(getApplicationContext());

        VersionLocalDataSource versionLocalDataSource = VersionLocalDataSource.getInstance(appExecutors, database.versionDao(), getApplicationContext());
        ToolLocalDataSource toolLocalDataSource = ToolLocalDataSource.getInstance(appExecutors, database.toolDao());
        NameDataSource nameLocalDataSource = NameLocalDataSource.getInstance(appExecutors, database.nameDao());
        LocalizedInfoLocalDataSource localizedInfoLocalDataSource = LocalizedInfoLocalDataSource.getInstance(new AppExecutors(), database.localizedInfoDao());
        ImagesLocalDataSource imagesLocalDataSource = ImagesLocalDataSource.getInstance(new AppExecutors(), database.imagesDao());

        versionLocalDataSource.clearTable();
        toolLocalDataSource.clearTable();
        nameLocalDataSource.clearTable();
        localizedInfoLocalDataSource.clearTable();
        imagesLocalDataSource.clearTable();

        versionLocalDataSource.saveVersion(config.getVersions().getAndroid());

        for (Category category : config.getCategories()) {
            category.getName().setCategoryId(category.getId());
            if (category.getIcon() != null) {
                category.getName().setIcon(category.getIcon().getUrl());
            }
            nameLocalDataSource.saveNames(category.getName());
        }

        for (Version version : config.getVersions().getAndroid()) {
            version.getImages().setToolId(version.getToolId());
            version.getImages().setVersionId(version.getId());
            imagesLocalDataSource.saveImages(version.getImages());
        }

        toolLocalDataSource.saveTool(config.getTools());

        for (Tool tool : config.getTools()) {
            toolLocalDataSource.saveTool(tool);

            tool.getImages().setToolId(tool.getId());
            imagesLocalDataSource.saveImages(tool.getImages());

            LocalizedInfo infoEn = tool.getInfo().getEn();
            infoEn.setToolId(tool.getId());
            infoEn.setLocale(EN);
            localizedInfoLocalDataSource.saveLocalizedInfo(infoEn);

            LocalizedInfo infoFa = tool.getInfo().getFa();
            infoFa.setToolId(tool.getId());
            infoFa.setLocale(FA);
            localizedInfoLocalDataSource.saveLocalizedInfo(infoFa);
        }
    }
}
