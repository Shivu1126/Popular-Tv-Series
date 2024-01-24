package com.sivaram.populartvseries.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sivaram.populartvseries.model.HomeTvSeries;

import java.util.List;

@Dao
public interface TvSeriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addTvSeries(HomeTvSeries homeTvSeries);

    @Query("select * from TvSeries")
    public List<HomeTvSeries> getAllSeries();

    @Query("select * from TvSeries where series_name like :searchStr")
    public List<HomeTvSeries> getSearchedSeries(String searchStr);

}
