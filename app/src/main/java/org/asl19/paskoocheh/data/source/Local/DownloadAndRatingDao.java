package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.DownloadAndRating;

import java.util.List;

@Dao
public interface DownloadAndRatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(DownloadAndRating... downloadAndRatings);

    @Delete
    void delete(DownloadAndRating downloadAndRating);

    @Query("SELECT * FROM downloadandrating WHERE toolId LIKE :toolId AND 'android' LIKE platformName LIMIT 1")
    DownloadAndRating getToolVersionDownloadAndRating(long toolId);

    @Query("SELECT * FROM downloadandrating WHERE 'android' LIKE platformName")
    List<DownloadAndRating> getDownloadAndRatings();

    @Query("SELECT * FROM downloadandrating WHERE 'android' LIKE platformName ORDER BY downloadCount DESC")
    List<DownloadAndRating> getDownloadAndRatingsDownloadCountDesc();

    @Query("DELETE FROM downloadandrating")
    void clearTable();
}
