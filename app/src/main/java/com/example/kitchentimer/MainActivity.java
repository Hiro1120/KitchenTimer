package com.example.kitchentimer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView mTimerText;
    ImageView mTimer_start;
    ImageView mTimer_stop;

    SoundPool mSoundPoll;
    int mSoundResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerText = (TextView) findViewById(R.id.timerText);
        mTimer_start = (ImageView) findViewById(R.id.timer_start);
        mTimer_stop = (ImageView) findViewById(R.id.timer_stop);

        //CountDownTimer(カウントダウンする秒数, onTick()メソッドを呼び出す間隔)
        CountDownTimer timer = new CountDownTimer(3*60*1000, 100) {
            //指定した感覚で実行したい処理
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long l) { //onTick(残り時間)
                long minute = l / 1000 / 60; //ミリ秒で受け取った数字を分単位に変換
                long second = l / 1000 % 60; //ミリ秒で受け取った数字を秒単位に変換

                mTimerText.setText(String.format("%1$d:%2$02d", minute, second));
            }
            //タイマー終了時の処理
            @Override
            public void onFinish() {
                mTimerText.setText("0:00");
                //(サウンドID,左側ボリューム,左側ボリューム,優先順位,ループ回数(0 or -1),再生速度)
                mSoundPoll.play(mSoundResId, 1.0f, 1.0f, 0, -1, 1.0f);
            }
        };

        //タイマーをスタートした時の処理
        mTimer_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timer.start();
            }
        });

        //タイマーをストップした時の処理
        mTimer_stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timer.cancel();
                mSoundPoll.stop(mSoundResId);
            }
        });
    }

    //画面が表示された時に呼ばれるメソッド
    @Override
    protected void onResume() {
        super.onResume();
        mSoundPoll = new SoundPool.Builder() //ビルダーの取得
                //ビルダーに対する設定
                .setMaxStreams(1) //同時に再生することのできる音源数
                .setAudioAttributes(new AudioAttributes.Builder()//オーディオのプロパティを設定
                        .setUsage(AudioAttributes.USAGE_ALARM) //使用方法（アラーム用の音源）
                        .build())
                .build(); //サウンドプールが生成されて、mSoundPollに格納される
        //バージョン分岐（API Level21より低いバージョンでもサウンドプールを使用可能とするため）
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            mSoundPoll = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build())
                    .build();
        }

        //loadメソッドでサウンドファイルを読み込む(コンテキスト,読み込むサウンドファイル,優先順位)
        mSoundResId = mSoundPoll.load(this, R.raw.bellsound, 1);
    }

    //画面が閉じられた時に呼ばれるメソッド
    @Override
    protected void onPause() {
        super.onPause();
        mSoundPoll.release(); //リソースの解放（サウンドプールが使用しているデータをリリース）
    }
}