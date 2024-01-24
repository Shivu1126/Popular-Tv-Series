package com.sivaram.populartvseries.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TvSeries")
public class HomeTvSeries {

    @ColumnInfo(name = "series_id")
    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "poster_url")
    private String posterUrl;

    @ColumnInfo(name = "vote_avg")
    private double voteAvg;

    @ColumnInfo(name = "series_name")
    private String seriesName;

    @ColumnInfo(name = "publish_date")
    private String publishedDate;

    public HomeTvSeries(int id, String posterUrl, double voteAvg, String seriesName, String publishedDate) {
        this.id = id;
        this.posterUrl = posterUrl;
        this.voteAvg = voteAvg;
        this.seriesName = seriesName;
        this.publishedDate = publishedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public double getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(double voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
