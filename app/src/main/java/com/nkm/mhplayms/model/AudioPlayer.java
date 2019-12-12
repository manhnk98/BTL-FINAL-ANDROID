package com.nkm.mhplayms.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Comparator;

public class AudioPlayer implements Serializable {
    private String path;
    private String name;
    private String tenCS;
    private String album;
    private int duration;
    private Bitmap imgBitSong;

    public AudioPlayer() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenCS() {
        return tenCS;
    }

    public void setTenCS(String tenCS) {
        this.tenCS = tenCS;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Bitmap getImgBitSong() {
        return imgBitSong;
    }

    public void setImgBitSong(Bitmap imgBitSong) {
        this.imgBitSong = imgBitSong;
    }

    public static class SortByName implements Comparator<AudioPlayer> {
        @Override
        public int compare(AudioPlayer o1, AudioPlayer o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
