package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Name;

import java.util.List;

@Dao
public interface NameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Name... names);

    @Delete
    void delete(Name name);

    @Query("SELECT * FROM name ORDER BY categoryId")
    List<Name> getNames();

    @Query("DELETE FROM name")
    void clearTable();
}
