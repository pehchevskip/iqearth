package com.pehchevskip.iqearth.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;

import java.util.List;

/**
 * Created by pehchevskip on 18-May-18.
 */

@Dao
public interface DaoAnimals {

    @Query("select * from animal")
    public List<EntityAnimal> getAnimals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAnimals(List<EntityAnimal> entityAnimals);
}
