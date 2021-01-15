package org.asl19.paskoocheh.data.source.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Tool;

import java.util.List;

@Dao
public interface ToolDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Tool... tools);

    @Delete
    void delete(Tool versions);

    @Query("SELECT * FROM tool")
    List<Tool> getAllTools();

    @Query("SELECT * FROM tool WHERE id = :toolId LIMIT 1")
    Tool getTool(long toolId);

    @Query("DELETE FROM tool")
    void clearTable();
}
