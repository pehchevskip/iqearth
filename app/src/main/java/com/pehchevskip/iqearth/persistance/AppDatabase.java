package com.pehchevskip.iqearth.persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pehchevskip.iqearth.persistance.dao.DaoCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;

/**
 * Created by pehchevskip on 18-May-18.
 */

@Database(entities = {EntityCountry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DaoCountry daoCountry();
}
