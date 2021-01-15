package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.LastModified;

@Dao
public interface LastModifiedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LastModified... lastModifieds);

    @Delete
    void delete(LastModified lastModified);

    @Query("SELECT * FROM lastmodified WHERE configFile LIKE :configName")
    LastModified getLastModified(String configName);

    @Query("DELETE FROM lastmodified")
    void clearTable();
}

