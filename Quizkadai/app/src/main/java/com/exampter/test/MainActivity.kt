package com.exampter.test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exampter.test.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.util.Collections
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityMainBinding? = null

    private var rightAnswerCount = 0
    private var quizCount = 1
    private var rightAnswer: String? = null
    private var score = 0
    private var correctStreak = 0

    private val quizArray = ArrayList<ArrayList<String?>?>()
    private val quizData = arrayOf(
        arrayOf(
            "「I’ll Kill You」と「I’ll Kill You」は彼の最も有名な曲です、メグミ。",
            "呪術廻戦",
            "長崎市",
            "福島市",
            "前橋市"
        ),
        arrayOf(
            "走らないで、走らないで、走らないで。",
            "エヴァンゲリオン",
            "広島市",
            "甲府市",
            "岡山市"
        ),
        arrayOf(
            "たとえ彼が邪魔をしたり、裏切ったり、私が世間知らずだと言ったとしても、私は彼を信じたかったのです！",
            "約束のネバーランド",
            "大分市",
            "秋田市",
            "福岡市"
        ),
        arrayOf(
            "主人公の仕事は、人生という冒険の中で偉業を成し遂げることです。",
            "僕のヒーローアカデミア",
            "水戸市",
            "岐阜市",
            "福井市"
        ),
        arrayOf(
            "私は二度逮捕されました。父は一度も私を叩いたことがありません!",
            "機動戦士ガンダム",
            "横浜市",
            "鳥取市",
            "仙台市"
        ),
        arrayOf("痩せたいならこれを食べなさい！！！", "バキ", "青森市", "山口市", "奈良市"),
        arrayOf(
            "質問に質問で答えないでください!!生徒たちに質問に質問で答えることを教えているのでしょうか?",
            "ジョジョの奇妙な冒険",
            "盛岡市",
            "新宿区",
            "京都市"
        ),
        arrayOf(
            "生きるか死ぬかを他人に決めさせないでください!",
            "鬼滅の刃",
            "金沢市",
            "名古屋市",
            "奈良市"
        ),
        arrayOf("また会ったら友達と呼んでくれますか？", "ONE PIECE", "札幌市", "岡山市", "奈良市"),
        arrayOf("包帯を巻いてくれてありがとう", "進撃の巨人", "福岡市", "松江市", "福井市"),
        arrayOf("めっちゃ寒いです…！", "カイジ", "長崎市", "福島市", "前橋市"),
        arrayOf(
            "しかし、私のような凡人には下を向いている暇があるだろうか？",
            "ハイキュー!!",
            "広島市",
            "甲府市",
            "岡山市"
        ),
        arrayOf(
            "不思議！ 「愛している」…知りたい",
            "ヴァイオレット・エヴァーガーデン",
            "大分市",
            "秋田市",
            "福岡市"
        ),
        arrayOf(
            "これはジェッドアームストロングの上にニールアームストロングが乗っているのでしょうか？",
            "銀魂",
            "水戸市",
            "岐阜市",
            "福井市"
        ),
        arrayOf("止まったよ────止まれ！", "NARUTO -ナルト-", "横浜市", "鳥取市", "仙台市"),
        arrayOf(
            "立ち上がって、歩いて、前へ進みましょう。あなたの足は大丈夫です。",
            "鋼の錬金術師",
            "青森市",
            "山口市",
            "奈良市"
        ),
        arrayOf("彼は新世界の神となった。", "DEATH NOTE -デスノート-", "盛岡市", "新宿区", "京都市"),
        arrayOf("君は死んだ！", "北斗の拳", "金沢市", "名古屋市", "奈良市"),
        arrayOf(
            "さて、これが最後です。頑張って下さい。",
            "HUNTER×HUNTER",
            "札幌市",
            "岡山市",
            "奈良市"
        ),
        arrayOf(
            "十分な経験があれば、弱者でもエリートに勝つことができます。",
            "ドラゴンボール",
            "福岡市",
            "松江市",
            "福井市"
        ),
    )

    private var playerName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val roomId = "12345"
        val roomRef = FirebaseDatabase.getInstance().getReference("rooms/$roomId")

        val PN = "player1"
        val playerData = mapOf(
            "name" to PN,
            "ready" to "false",
            "score" to 0
        )

        val PN2 = "player2"
        val playerData2 = mapOf(
            "name" to PN2,
            "ready" to "false",
            "score" to 0
        )

        roomRef.child(PN).setValue(playerData)
        roomRef.child(PN2).setValue(playerData2)

        roomRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val player1 = snapshot.child("player1").child("name").getValue(String::class.java)
                val player2 = snapshot.child("player2").child("name").getValue(String::class.java)

                if(player1 != null && player2 != null)
                {
                    //両方そろってるのでゲーム開始
                    Log.d("Firebase","2人そろいました!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase","データ習得失敗:${error.message}")
            }
        })

        roomRef.get().addOnSuccessListener { dataSnapshot ->
            val name = dataSnapshot.child("name").getValue(String::class.java)
            Log.d("Firebase", "プレイヤー名: $name")
        }.addOnFailureListener {
            Log.e("Firebase", "取得に失敗: ${it.message}")
        }

        playerName = intent.getStringExtra("playerName")
        if (playerName == null) playerName = "ゲスト"

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding!!.answerBtn1.setOnClickListener(this)
        binding!!.answerBtn2.setOnClickListener(this)
        binding!!.answerBtn3.setOnClickListener(this)
        binding!!.answerBtn4.setOnClickListener(this)

        for (quizDatum in quizData) {
            val tmpArray = ArrayList<String?>()
            Collections.addAll(tmpArray, *quizDatum)
            quizArray.add(tmpArray)
        }

        Collections.shuffle(quizArray)
        showNextQuiz()
    }

    private fun showNextQuiz() {
        binding!!.countLabel.text = getString(R.string.count_label, quizCount)

        val quiz = ArrayList(quizArray.removeAt(0))
        binding!!.questionLabel.text = quiz[0]

        rightAnswer = quiz[1]
        quiz.removeAt(0)
        quiz.removeAt(0)

        quiz.add(rightAnswer)
        Collections.shuffle(quiz)

        binding!!.answerBtn1.text = quiz[0]
        binding!!.answerBtn2.text = quiz[1]
        binding!!.answerBtn3.text = quiz[2]
        binding!!.answerBtn4.text = quiz[3]
    }

    override fun onClick(v: View) {
        val answerBtn = v as Button
        val btnText = answerBtn.text.toString()

        val alertTitle: String
        if (btnText == rightAnswer) {
            alertTitle = "正解!"
            correctStreak++
            score += correctStreak
            rightAnswerCount++
        } else {
            alertTitle = "不正解..."
            correctStreak = 0
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(alertTitle)
            .setMessage("答え : $rightAnswer")
            .setPositiveButton(
                "OK"
            ) { dialogInterface, i ->
                if (quizCount == QUIZ_COUNT) {
                    val prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE)
                    val editor = prefs.edit()
                    var totalScore = prefs.getInt(playerName + "_total_score", 0)
                    totalScore += score
                    editor.putInt(playerName + "_total_score", totalScore)
                    editor.apply()

                    val intent = Intent(
                        this@MainActivity,
                        ResultActivity::class.java
                    )
                    intent.putExtra("RIGHT_ANSWER_COUNT", rightAnswerCount)
                    intent.putExtra("SCORE", score)
                    intent.putExtra("playerName", playerName)
                    startActivity(intent)
                    finish()
                } else {
                    quizCount++
                    showNextQuiz()
                }
            }
            .setCancelable(false)
            .show()
    }

    companion object {
        private const val QUIZ_COUNT = 10
    }
}
