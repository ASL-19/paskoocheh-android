package org.asl19.paskoocheh.data.source.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Images;

import java.util.List;

@Dao
public interface ImagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Images... images);

    @Delete
    void delete(Images images);

    @Query("SELECT * FROM images WHERE versionId = :versionId LIMIT 1")
    Images getVersionImages(long versionId);

    @Query("SELECT * FROM images WHERE toolId = :toolId LIMIT 1")
    Images getToolImages(long toolId);

    @Query("SELECT * FROM images")
    List<Images> getImages();

    @Query("DELETE FROM images")
    void clearTable();
}