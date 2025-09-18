package com.exampter.test

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.exampter.test.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase

class TitleActivity : AppCompatActivity() {
    private var bgmPlayer: MediaPlayer? =null  //BGM用のプレイヤー

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        //bgm再生の準備
        bgmPlayer = MediaPlayer.create(this, R.raw.kuizetitle)
        bgmPlayer?.isLooping = true //ループ再生
        bgmPlayer?.start()


        val startButton = findViewById<Button>(R.id.startButton)        //ボタン操作できるように
        //ボタンをクリックしたときにおこることを登録
        startButton.setOnClickListener { v: View? ->
            // 名前入力なしで「ゲスト」として開始
            val playerName = "ゲスト"  //名前入力するところあったっけ？
            val intent = Intent(this@TitleActivity, HomeActivity::class.java)       //次の画面へ
            intent.putExtra("playerName", playerName)       //持っていくものを設定
            startActivity(intent)
            finish()
        }
    }

    override fun onPause()
    {
        super.onPause()
        //画面を離れたら音を止める
        if(bgmPlayer?.isPlaying == true)
        {
            bgmPlayer?.pause()
        }
    }

    override fun onResume()
    {
        super.onResume()
        //戻ってきたら音を再開
        if(bgmPlayer?.isPlaying ==false)
        {
            bgmPlayer?.start()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        //リソース開放
        bgmPlayer?.release()
        bgmPlayer = null
    }


}
