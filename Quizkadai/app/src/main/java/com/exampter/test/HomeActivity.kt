package com.exampter.test

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool //効果音用
    private var soundId: Int = 0              //効果音ID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        //効果音の準備
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool.load(this, R.raw.buttn, 1) //se再生

        var roomIdInput = findViewById<EditText>(R.id.editTextText)
        val onebutton = findViewById<Button>(R.id.OneButton)        //ボタン操作できるように
        val twobutton = findViewById<Button>(R.id.TwoButton)        //ボタン操作できるように
        //ボタンをクリックしたときにおこることを登録
        onebutton.setOnClickListener { v: View? ->
            //効果音を鳴らす
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
            // 名前入力なしで「ゲスト」として開始
            val playerName = intent.getStringExtra("playerName")
            // 一人用のシーンへ
            val intent = Intent(this@HomeActivity, SinglgameActivity::class.java)//いずれ変える
            intent.putExtra("playerName", playerName)       //持っていくものを設定
            startActivity(intent)
            finish()
        }

        twobutton.setOnClickListener{ v: View? ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
            //二人プレイを押した場合
            //入力値を文字列に変換。前後の空白を削除
            val roomId = roomIdInput.text.toString().trim()

            if (roomId.isEmpty())
            {
                //何もなかったらユーザーにフィードバック
                Toast.makeText(this,"ルームIDを入力してください",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val playerName = intent.getStringExtra("playerName")
            // 二人用のシーンへ
            val intent = Intent(this@HomeActivity, Serching::class.java).apply {
                putExtra("roomID",roomId)
                putExtra("playerName",playerName)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //リソース開放
        soundPool.release()
    }
}