package com.sivaram.populartvseries.model;

public class HomeTvSeries {
    private int id;
    private String posterUrl;
    private double voteAvg;
    private String seriesName;
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
