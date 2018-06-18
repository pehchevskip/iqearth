package com.pehchevskip.iqearth.persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pehchevskip.iqearth.persistance.dao.DaoAnimals;
import com.pehchevskip.iqearth.persistance.dao.DaoCountry;
import com.pehchevskip.iqearth.persistance.dao.DaoCity;
import com.pehchevskip.iqearth.persistance.dao.DaoMountain;
import com.pehchevskip.iqearth.persistance.dao.DaoPlayer;
import com.pehchevskip.iqearth.persistance.entities.EntityAnimal;
import com.pehchevskip.iqearth.persistance.entities.EntityCity;
import com.pehchevskip.iqearth.persistance.entities.EntityCountry;
import com.pehchevskip.iqearth.persistance.entities.EntityMountain;
import com.pehchevskip.iqearth.persistance.entities.EntityPlayer;

/**
 * Created by pehchevskip on 18-May-18.
 */

@Database(entities = {EntityCountry.class, EntityAnimal.class, EntityCity.class, EntityMountain.class, EntityPlayer.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DaoCountry daoCountry();
    public abstract DaoAnimals daoAnimals();
    public abstract DaoCity daoCity();
    public abstract DaoMountain daoMountain();
    public abstract DaoPlayer daoPlayer();
}
