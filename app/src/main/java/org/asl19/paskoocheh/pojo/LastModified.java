package org.asl19.paskoocheh.pojo;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import lombok.Data;

@Entity
@Data
public class LastModified {

    @PrimaryKey
    @NonNull
    public String configFile = "";

    public long lastModified = 0;

    public LastModified() {}
}
