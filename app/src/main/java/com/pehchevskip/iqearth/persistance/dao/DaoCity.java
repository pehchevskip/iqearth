package com.pehchevskip.iqearth.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pehchevskip.iqearth.persistance.entities.EntityCity;

import java.util.List;

/**
 * Created by pehchevskip on 19-May-18.
 */

@Dao
public interface DaoCity {

    @Query("select * from city")
    public List<EntityCity> getCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCities(List<EntityCity> entityCities);

}
