package com.pehchevskip.iqearth.persistance.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pehchevskip.iqearth.persistance.entities.EntityPlayer;

import java.util.List;

@Dao
public interface DaoPlayer {

    @Query("select * from player")
    public List<EntityPlayer> getPlayers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPlayer(EntityPlayer player);

    @Query("delete from player")
    public void deleteAll();

}
