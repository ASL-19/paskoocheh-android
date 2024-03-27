package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import org.asl19.paskoocheh.pojo.AppDownloadInfoForVersionCodesTypeConverter;
import org.asl19.paskoocheh.pojo.DevicesTypeConverter;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Faq;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LastModified;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.pojo.Text;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Tutorial;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.utils.ImageArrayTypeConverter;
import org.asl19.paskoocheh.utils.StringArrayTypeConverter;

@Database(entities = {Version.class, Faq.class, Tool.class, LocalizedInfo.class, DownloadAndRating.class, Guide.class, Tutorial.class, Review.class, Images.class, Name.class, LastModified.class, Text.class}, version = 42, exportSchema = false)
@TypeConverters({StringArrayTypeConverter.class, ImageArrayTypeConverter.class, DevicesTypeConverter.class, AppDownloadInfoForVersionCodesTypeConverter.class})
public abstract class PaskoochehDatabase extends RoomDatabase {

    private static PaskoochehDatabase INSTANCE;

    public abstract VersionDao versionDao();

    public abstract FaqDao faqDao();

    public abstract ToolDao toolDao();

    public abstract LocalizedInfoDao localizedInfoDao();

    public abstract DownloadAndRatingDao downloadAndRatingDao();

    public abstract GuideDao guideDao();

    public abstract TutorialDao tutorialDao();

    public abstract ReviewDao reviewDao();

    public abstract ImagesDao imagesDao();

    public abstract NameDao nameDao();

    public abstract TextDao textDao();

    public abstract LastModifiedDao lastModifiedDao();

    private static final Object sLock = new Object();

    public static PaskoochehDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        PaskoochehDatabase.class, "Paskoocheh.db")
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }
}
