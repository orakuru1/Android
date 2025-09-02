package com.exampter.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Serching : AppCompatActivity() {

    private lateinit var roomRef: DatabaseReference
    private val roomid = "12345"
    private var myPlayerKey = ""

    private val questionID = listOf("quiz1","quiz2","quiz3","quiz4","quiz5","quiz6","quiz7","quiz8","quiz9","quiz10")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serching)

    roomRef = FirebaseDatabase.getInstance().getReference("room/$roomid")

        val playerName = intent.getStringExtra("playerName") ?: "ゲスト"

        roomRef.get().addOnSuccessListener { snapshot ->    //get()やsetValue()は非同期なので、成功したときにやる
            val player1Exists = snapshot.hasChild("player1")
            myPlayerKey = if (player1Exists) "player2" else "player1"

            val playerData = mapOf(
                "name" to playerName,
                "ready" to false,
                "score" to 0
            )

            //プレイヤー１だったら問題のIDを持ってきて、シャッフルして、アップロードする
            if(myPlayerKey == "player1")//シャッフルしたデータをデータベースに送る実験中
            {
                val shuffleQuestion = questionID.shuffled()

                val updates = mapOf("quizList" to shuffleQuestion)
                roomRef.updateChildren(updates)

                roomRef.child("questionsID").setValue(shuffleQuestion)

            }


            roomRef.child(myPlayerKey).setValue(playerData).addOnSuccessListener {
                //roomRef.child(myPlayerKey).onDisconnect().removeValue()  // ネット切断・アプリ強制終了時にも自動で削除
                // ここで listener を登録する
                roomRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val player1 = snapshot.child("player1").child("name").getValue(String::class.java)
                        val player2 = snapshot.child("player2").child("name").getValue(String::class.java)

                        if (player1 != null && player2 != null) {
                            Log.d("Firebase", "2人そろいました!")
                            roomRef.removeEventListener(this)

                            val intent = Intent(this@Serching, MainActivity::class.java)
                            intent.putExtra("playerName", playerName)
                            intent.putExtra("roomID", roomid)
                            intent.putExtra("myPlayerKey", myPlayerKey)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "データ取得失敗: ${error.message}")
                    }
                })

            }
        }
    }
}