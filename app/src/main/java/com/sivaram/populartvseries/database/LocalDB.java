package com.sivaram.populartvseries.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sivaram.populartvseries.model.HomeTvSeries;

@Database(entities = {HomeTvSeries.class}, version = 1)
public abstract class LocalDB extends RoomDatabase {
    private static LocalDB database;
    private static String DATABASE_NAME = "TvSeriesApp";

    public synchronized static LocalDB getInstance(Context context){
        if(database==null){
            database = Room.databaseBuilder(context.getApplicationContext(), LocalDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    public abstract TvSeriesDao tvSeriesDao();
}
