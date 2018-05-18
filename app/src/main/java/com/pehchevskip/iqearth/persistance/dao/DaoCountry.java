package com.pehchevskip.iqearth.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pehchevskip.iqearth.persistance.entities.EntityCountry;

import java.util.List;

/**
 * Created by pehchevskip on 18-May-18.
 */

@Dao
public interface DaoCountry {

    @Query("select * from country")
    public List<EntityCountry> getCountries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCountries(List<EntityCountry> entityCountries);

}
