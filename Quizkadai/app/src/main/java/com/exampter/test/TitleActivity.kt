package com.exampter.test

import android.content.Intent
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

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
}
