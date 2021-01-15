package org.asl19.paskoocheh.data.source.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.LocalizedInfo;

import java.util.List;

@Dao
public interface LocalizedInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LocalizedInfo... info);

    @Delete
    void delete(LocalizedInfo info);

    @Query("SELECT * FROM localizedinfo WHERE toolId = :toolId")
    List<LocalizedInfo> getToolLocalizedInfo(long toolId);

    @Query("SELECT * FROM localizedinfo")
    List<LocalizedInfo> getLocalizedInfoList();

    @Query("DELETE FROM localizedinfo")
    void clearTable();
}