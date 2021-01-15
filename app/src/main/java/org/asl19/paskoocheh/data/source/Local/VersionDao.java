package org.asl19.paskoocheh.data.source.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

@Dao
public interface VersionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Version... versions);

    @Delete
    void delete(Version versions);

    @Query("SELECT * FROM version")
    List<Version> getAllAndroidVersions();

    @Query("SELECT * FROM version WHERE toolId LIKE :toolId")
    Version getVersion(long toolId);

    @Query("DELETE FROM version")
    void clearTable();

    @Query("SELECT * FROM version ORDER BY releaseDate DESC")
    List<Version> getUpdatedAndroidVersions();
}
