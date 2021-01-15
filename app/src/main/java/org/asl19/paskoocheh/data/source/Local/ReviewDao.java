package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Review;

import java.util.List;

@Dao
public interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Review... reviewses);

    @Delete
    void delete(Review reviews);

    @Query("SELECT * FROM review WHERE toolId LIKE :toolId AND platformName LIKE 'android' ORDER BY id DESC" )
    List<Review> getToolReview(int toolId);

    @Query("DELETE FROM review")
    void clearTable();
}
