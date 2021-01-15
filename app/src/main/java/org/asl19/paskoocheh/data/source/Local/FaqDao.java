package org.asl19.paskoocheh.data.source.Local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.asl19.paskoocheh.pojo.Faq;

import java.util.List;

@Dao
public interface FaqDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Faq... faqs);

    @Delete
    void delete(Faq faq);

    @Query("SELECT * FROM faq WHERE toolId LIKE :toolId AND language LIKE 'fa' ORDER BY 'order' ASC")
    List<Faq> getToolVersionFaqs(int toolId);

    @Query("DELETE FROM faq")
    void clearTable();
}
