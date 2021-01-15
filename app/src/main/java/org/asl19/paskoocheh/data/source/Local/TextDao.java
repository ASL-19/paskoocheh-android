package org.asl19.paskoocheh.data.source.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Text;

import java.util.List;

@Dao
public interface TextDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Text... texts);

    @Delete
    void delete(Text text);

    @Query("SELECT * FROM text")
    List<Text> getPaskoochehTexts();

    @Query("DELETE FROM text")
    void clearTable();
}
