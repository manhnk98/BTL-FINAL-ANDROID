package com.nkm.mhplayms.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkm.mhplayms.Cost;
import com.nkm.mhplayms.R;
import com.nkm.mhplayms.model.AudioPlayer;

import java.util.ArrayList;
import java.util.List;

public class ListSingAdapter extends BaseAdapter {

    private List<AudioPlayer> audioPlayers;
    private List<ImageView> imageViews;
    private Context context;

    private int playing = -1;
    private String status = Cost.PAUSE;

    public ListSingAdapter(Context context, List<AudioPlayer> audioPlayers) {
        this.context = context;
        this.audioPlayers = audioPlayers;
        imageViews = new ArrayList<>();
    }

    public void setAudioPlayers(List<AudioPlayer> audioPlayers) {
        this.audioPlayers = audioPlayers;
    }

    @Override
    public int getCount() {
        return audioPlayers.size();
    }

    @Override
    public Object getItem(int position) {
        return audioPlayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HollerView hollerView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_sing, parent, false);
            hollerView = new HollerView();
            hollerView.tvTenBaiHat = convertView.findViewById(R.id.tv_title_of_sing);
            hollerView.tvAlbum = convertView.findViewById(R.id.tv_album_of_sing1);
            hollerView.tvTenCaSi = convertView.findViewById(R.id.tv_artist_of_sing);
            hollerView.imPlaying = convertView.findViewById(R.id.im_sing_is_playing);
            hollerView.imgSong = convertView.findViewById(R.id.imgSong);
            convertView.setTag(hollerView);
        } else {
            hollerView = (HollerView) convertView.getTag();
        }
        AudioPlayer audioPlayer = (AudioPlayer) getItem(position);
        hollerView.tvTenCaSi.setText(audioPlayer.getTenCS());
        hollerView.tvTenBaiHat.setText(audioPlayer.getName());
        hollerView.tvAlbum.setText(audioPlayer.getAlbum());
        if (audioPlayer.getImgBitSong() != null) {
            hollerView.imgSong.setImageBitmap(audioPlayer.getImgBitSong());
        } else {
            hollerView.imgSong.setImageResource(R.drawable.icon_singer);
        }

        imageViews.add(hollerView.imPlaying);
        if (position != playing || status.equals(Cost.PAUSE)) {
            hollerView.imPlaying.setVisibility(View.INVISIBLE);
        } else {
            hollerView.imPlaying.setVisibility(View.VISIBLE);
            AnimationDrawable animationUtils = (AnimationDrawable) hollerView.imPlaying.getDrawable();
            animationUtils.start();
        }
        return convertView;
    }

    private class HollerView {
        private TextView tvTenBaiHat;
        private TextView tvTenCaSi;
        private TextView tvAlbum;
        private ImageView imPlaying;
        private ImageView imgSong;
    }
}
