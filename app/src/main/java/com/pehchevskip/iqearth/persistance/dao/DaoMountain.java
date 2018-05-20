package com.pehchevskip.iqearth.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pehchevskip.iqearth.persistance.entities.EntityMountain;

import java.util.List;

/**
 * Created by pehchevskip on 19-May-18.
 */

@Dao
public interface DaoMountain {

    @Query("select * from mountain")
    public List<EntityMountain> getMountains();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMountains(List<EntityMountain> entityMountains);

}
