package com.nkm.mhplayms.model;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ManagerMedia {
    private Activity mContext;

    public ManagerMedia(Activity mContext) {
        this.mContext = mContext;
    }

    public List<AudioPlayer> getListAudioPlayer() {
        List<AudioPlayer> songs = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(mContext,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(mContext,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int songLocation = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int indexName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int indexAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                do {
                    if (cursor.getString(songLocation).endsWith(".mp3") || cursor.getString(songLocation).endsWith(".flac")) {
                        AudioPlayer audioPlayer = new AudioPlayer();
                        String data = cursor.getString(songLocation);
                        String name = cursor.getString(indexName);
                        int duration = cursor.getInt(indexDuration);
                        String album = cursor.getString(indexAlbum);
                        String artist = cursor.getString(indexArtist);

                        audioPlayer.setPath(data);
                        audioPlayer.setName(name);
                        audioPlayer.setAlbum(album);
                        audioPlayer.setTenCS(artist);
                        audioPlayer.setDuration(duration);
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(data);
                        byte[] dataImg = mediaMetadataRetriever.getEmbeddedPicture();
                        if (dataImg != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(dataImg, 0, dataImg.length);
                            audioPlayer.setImgBitSong(bitmap);
                        }
                        songs.add(audioPlayer);
                    }

                } while (cursor.moveToNext());
            }
        }
        Collections.sort(songs, new AudioPlayer.SortByName());
        return songs;
    }
}
