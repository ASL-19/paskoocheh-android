package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Tutorial;

import java.util.List;

@Dao
public interface TutorialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Tutorial... tutorials);

    @Delete
    void delete(Tutorial tutorial);

    @Query("SELECT * FROM tutorial WHERE toolId LIKE :toolId ORDER BY `order` ASC")
    List<Tutorial> getToolTutorial(int toolId);

    @Query("DELETE FROM tutorial")
    void clearTable();
}
