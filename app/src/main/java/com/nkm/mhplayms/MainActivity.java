package com.nkm.mhplayms;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nkm.mhplayms.adapter.ListSingAdapter;
import com.nkm.mhplayms.model.AudioPlayer;
import com.nkm.mhplayms.model.ManagerMedia;
import com.nkm.mhplayms.shake.ShakeDetector;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private RotateAnimation rotateAnimation;
    private ImageView imPlaydisc;
    private ImageButton imPlay;
    private ImageButton imNext;
    private ImageView imgSong;

    private SharedPreferences preferences;

    private SlidingUpPanelLayout mLayout;
    private SeekBar positionBar;

    private ImageButton btnBackPlay;
    private ImageButton btnPauseAndPlay;
    private ImageButton btnNextAndPlay;
    private ImageButton btnPressAndPlay;
    private ImageButton btnLap;
    private ImageButton btnTron;

    private TextView tvSingNamePlay;
    private TextView tvArtistNamePlay;
    private TextView tvAlbumNamePlay;
    private TextView tvNameSing;
    private TextView tvNameArtist;
    private TextView elapsedTimeLabel;
    private TextView remainingTimeLabel;
    private MediaPlayer mediaPlayer;
    private ManagerMedia managerMedia;

    private List<AudioPlayer> listSing;
    private int baiSo = -1;
    private ListView lvParent;
    private ListSingAdapter listPlayingAdapter;

    private String typePlayMusic = Cost.NOREPEAT;
    int totalTime;
    Bitmap bitmap;

    // sensor
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    //
    int countRepeat = 0;
    int demRandom = 0;
    String randomSong = Cost.OFF;
    boolean checkShake = false;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListSing();
        initSensor();
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void playAudio() {
                if(checkShake)
                    nextMusic();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    private void initListSing() {
        managerMedia = new ManagerMedia(MainActivity.this);
        listSing = managerMedia.getListAudioPlayer();
        baiSo = -1;
        listPlayingAdapter = new ListSingAdapter(MainActivity.this, listSing);

        lvParent.setAdapter(listPlayingAdapter);
        lvParent.setOnItemClickListener(this);
        mediaPlayer = new MediaPlayer();
    }

    private void initViews() {
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mLayout = findViewById(R.id.sliding_layout_main);
        mLayout.setPanelHeight(0);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        lvParent = findViewById(R.id.lvParent);

        positionBar = findViewById(R.id.positionBar);

        preferences = getSharedPreferences(Cost.MY_APP, MODE_PRIVATE);

        //set image chuyen dong khi playing
        imPlaydisc = findViewById(R.id.image_sing);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotale_item_play);
        imPlaydisc.setAnimation(rotateAnimation);
        rotateAnimation.cancel();

        imPlay = findViewById(R.id.btn_play);
        imNext = findViewById(R.id.btn_next);

        imPlay.setOnClickListener(this);
        imNext.setOnClickListener(this);

        btnBackPlay = findViewById(R.id.btn_hiden_layout_play);
        btnNextAndPlay = findViewById(R.id.btn_next_play);
        btnPauseAndPlay = findViewById(R.id.btn_play_and_pause);
        btnPressAndPlay = findViewById(R.id.btn_press_play);
        btnLap = findViewById(R.id.btn_lap_bai);
        btnTron = findViewById(R.id.btn_tron);

        btnBackPlay.setOnClickListener(this);
        btnNextAndPlay.setOnClickListener(this);
        btnPauseAndPlay.setOnClickListener(this);
        btnPressAndPlay.setOnClickListener(this);
        btnLap.setOnClickListener(this);
        btnTron.setOnClickListener(this);

        tvAlbumNamePlay = findViewById(R.id.tv_album_name_play);
        tvArtistNamePlay = findViewById(R.id.tv_artist_name_play);
        tvSingNamePlay = findViewById(R.id.tv_sing_name_play);
        tvNameSing = findViewById(R.id.tv_name_sing);
        tvNameArtist = findViewById(R.id.tv_artist);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        imgSong = findViewById(R.id.imgSong);

        findViewById(R.id.btn_show_layout_play).setOnClickListener(this);
        findViewById(R.id.dragView).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.btn_play:
                playAndPauseMusic();
                break;
            case R.id.btn_next:
                nextMusic();
                break;
            case R.id.btn_hiden_layout_play:
                hidenLayoutPlay();
                break;
            case R.id.btn_next_play:
                nextMusic();
                break;
            case R.id.btn_press_play:
                pressMusic();
                break;
            case R.id.btn_play_and_pause:
                playAndPauseMusic();
                break;
            case R.id.btn_show_layout_play:
                showLayoutPlay();
                break;
            case R.id.btn_lap_bai:
                setLapLai();
                break;
            case R.id.btn_tron:
                setTronBai();
                break;
        }
    }

    private void playMusic(AudioPlayer audioPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(listSing.get(baiSo).getPath()));
        mediaPlayer.start();

        //set hinh anh
        rotateAnimation.start();
        imPlay.setImageResource(R.drawable.pause_button);
        btnPauseAndPlay.setImageResource(R.drawable.pause_button);
        tvSingNamePlay.setText(audioPlayer.getName());
        tvArtistNamePlay.setText(audioPlayer.getTenCS());
        tvAlbumNamePlay.setText(audioPlayer.getAlbum());
        tvNameSing.setText(audioPlayer.getName());
        tvNameArtist.setText(audioPlayer.getTenCS());
        totalTime = mediaPlayer.getDuration();

        bitmap = listSing.get(baiSo).getImgBitSong();
        if (bitmap == null) {
            imgSong.setImageResource(R.drawable.icon_singer);
        } else {
            imgSong.setImageBitmap(bitmap);
        }
        setupSeekBar();
    }

    private void initFirstSong(AudioPlayer audioPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(listSing.get(baiSo).getPath()));
        mediaPlayer.start();
        mediaPlayer.pause();

        //set hinh anh
        rotateAnimation.cancel();
        btnPauseAndPlay.setImageResource(R.drawable.music_player_play);
        imPlay.setImageResource(R.drawable.music_player_play);
        tvSingNamePlay.setText(audioPlayer.getName());
        tvArtistNamePlay.setText(audioPlayer.getTenCS());
        tvAlbumNamePlay.setText(audioPlayer.getAlbum());
        tvNameSing.setText(audioPlayer.getName());
        tvNameArtist.setText(audioPlayer.getTenCS());
        totalTime = mediaPlayer.getDuration();

        bitmap = listSing.get(baiSo).getImgBitSong();
        if (bitmap == null) {
            imgSong.setImageResource(R.drawable.icon_singer);
        } else {
            imgSong.setImageBitmap(bitmap);
        }
        setupSeekBar();
    }

    private void playAndPauseMusic() {
        if (baiSo == -1) {
            baiSo = 0;
            playMusic(listSing.get(baiSo));
            return;
        }

        //pause or start
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            rotateAnimation.cancel();
            btnPauseAndPlay.setImageResource(R.drawable.music_player_play);
            imPlay.setImageResource(R.drawable.music_player_play);
        } else {
            mediaPlayer.start();
            rotateAnimation.start();
            btnPauseAndPlay.setImageResource(R.drawable.pause_button);
            imPlay.setImageResource(R.drawable.pause_button);
        }
    }

    private void nextMusic() {
        //chinh bai hat
        if (randomSong.equals(Cost.ON)) {
            //truong hop tron lung tung
            Random random = new Random();
            int bai;
            do {
                bai = random.nextInt(listSing.size());
            } while (baiSo == bai);
            baiSo = bai;
        } else {
            //truong hop ko tron
            if (baiSo == -1 && listSing.size() > 0) {
                baiSo = 0;
            } else {
                if (baiSo == listSing.size() - 1) {
                    baiSo = 0;
                } else {
                    baiSo = baiSo + 1;
                }
            }
        }

        playMusic(listSing.get(baiSo));

    }

    private void pressMusic() {
        //chinh bai hat
        if (randomSong.equals(Cost.ON)) {
            //truong hop tron lung tung
            Random random = new Random();
            int bai;
            do {
                bai = random.nextInt(listSing.size());
            } while (baiSo == bai);
            baiSo = bai;
        } else {
            if (baiSo == -1 || baiSo == 0) {
                baiSo = listSing.size() - 1;

            } else {
                baiSo = baiSo - 1;
            }
        }
        playMusic(listSing.get(baiSo));

    }


    private void checkRandom() {
        if (randomSong.equals(Cost.ON)) {
            Random random = new Random();
            int bai;
            do {
                bai = random.nextInt(listSing.size());
            } while (baiSo == bai);
            baiSo = bai;
        }
    }

    private void hetBai() {
        if (typePlayMusic.equals(Cost.NOREPEAT)) {
            if ((baiSo == -1 && listSing.size() > 0) || (baiSo == listSing.size() - 1)) {
                baiSo = 0;
                initFirstSong(listSing.get(baiSo));
                return;
            } else {
                baiSo = baiSo + 1;
                checkRandom();
            }
        } else if (typePlayMusic.equals(Cost.REPEATALL)) {
            if ((baiSo == -1 && listSing.size() > 0) || (baiSo == listSing.size() - 1)) {
                baiSo = 0;
            } else {
                baiSo = baiSo + 1;
                checkRandom();
            }
        }
        playMusic(listSing.get(baiSo));
    }

    private void hidenLayoutPlay() {
        if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED ||
                mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    private void showLayoutPlay() {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    private void setTronBai() {
        // chan la OFF (khong tron)
        // le la ON (tron)
        demRandom++;
        if (demRandom == 1) {
            randomSong = Cost.ON;
            ImageViewCompat.setImageTintList(btnTron, getColorState(R.color.green));
        } else {
            randomSong = Cost.OFF;
            ImageViewCompat.setImageTintList(btnTron, getColorState(R.color.colorPrimary));
            demRandom = 0;
        }
    }

    private void setLapLai() {
        countRepeat++;
        if (countRepeat == 1) {
            typePlayMusic = Cost.REPEATALL;
            ImageViewCompat.setImageTintList(btnLap, getColorState(R.color.green));
            Toast.makeText(this, "REPEAT ALL", Toast.LENGTH_SHORT).show();
        } else if (countRepeat == 2) {
            Toast.makeText(this, "REPEAT ONE", Toast.LENGTH_SHORT).show();
            typePlayMusic = Cost.REPEATONE;
            btnLap.setImageResource(R.drawable.repeat1);
            ImageViewCompat.setImageTintList(btnLap, getColorState(R.color.green));
        } else {
            Toast.makeText(this, "NO REPEAT", Toast.LENGTH_SHORT).show();
            typePlayMusic = Cost.NOREPEAT;
            btnLap.setImageResource(R.drawable.repeat);
            ImageViewCompat.setImageTintList(btnLap, getColorState(R.color.colorPrimary));
            countRepeat = 0;
        }

    }

    private ColorStateList getColorState(int color) {
        return AppCompatResources.getColorStateList(MainActivity.this, color);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setPlaying(listSing, i);
    }

    private void setPlaying(List<AudioPlayer> listSing, int i) {
        this.baiSo = i;
        this.listSing = listSing;
        listPlayingAdapter.setAudioPlayers(listSing);
        listPlayingAdapter.notifyDataSetChanged();
        playMusic(listSing.get(i));
    }

    // =============================================================================================

    private void setupSeekBar() {
        // Position Bar
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                hetBai();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.setting:
                showDialogSetting();
                break;
            case R.id.support:
                Toast.makeText(this, "support", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void showDialogSetting() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCanceledOnTouchOutside(false);

        final Switch shakeSwitch = dialog.findViewById(R.id.shakeSwitch);
        shakeSwitch.setChecked(checkShake);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkShake = shakeSwitch.isChecked();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}
