package com.sivaram.populartvseries.model;

public class Cast {
    private String name;
    private String characterName;
    private String episodes;
    private String picUrlPath;

    public Cast(String name, String characterName, String episodes, String picUrlPath) {
        this.name = name;
        this.characterName = characterName;
        this.episodes = episodes;
        this.picUrlPath = picUrlPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public String getPicUrlPath() {
        return picUrlPath;
    }

    public void setPicUrlPath(String picUrlPath) {
        this.picUrlPath = picUrlPath;
    }
}
