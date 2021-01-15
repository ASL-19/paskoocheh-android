package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Guide;

import java.util.List;

@Dao
public interface GuideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Guide... guides);

    @Delete
    void delete(Guide guide);

    @Query("SELECT * FROM guide WHERE toolId LIKE :toolId ORDER BY 'order' ASC")
    List<Guide> getToolGuide(int toolId);

    @Query("DELETE FROM guide")
    void clearTable();

    @Query("SELECT * FROM guide WHERE id LIKE :guideId")
    Guide getGuide(int guideId);
}
