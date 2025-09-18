package com.exampter.test

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

data class Quiz(
    val question: String = "",
    val answer: String = "",
    val choices: List<String> = emptyList()
)

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityMainBinding? = null

    private var rightAnswerCount = 0
    private var quizCount = 1
    //private var rightAnswer: String? = null
    private var score = 0
    private var correctStreak = 0

    private val quizArray = ArrayList<ArrayList<String?>?>()

    val quizList = mutableListOf<Quiz>()

    val database = FirebaseDatabase.getInstance()
    val quizRef = database.getReference("quizness")

    private var alertTitle: String? = null

    private var playerName: String? = null

    private lateinit var roomRef: DatabaseReference
    private lateinit var myPlayerKey: String

    private val TOTAL_TIME = 30_000L    //30秒（ミリ単位）

    private var CountDownTimer:CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        //var roomID = intent.getStringExtra("roomID")

        myPlayerKey = intent.getStringExtra("myPlayerKey") ?: "nullplayer"

        val roomid = intent.getStringExtra("roomID")
        roomRef = database.getReference("room/$roomid")


        //その時点での値を習得して、リスナー解除
        quizRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                //二人いても、途中で一人が抜けたら、ルームがなくなってしまうのか？---------------------------------------------------
                //roomRef.child(myPlayerKey).onDisconnect().removeValue()  // ネット切断・アプリ強制終了時にも自動で削除
                ////////<<<<<<<<<<<<<<<<やること>>>>>>>>>>>>>>>>>>>>>>>>
                //制限時間を付ける、１問当たりの時間、早押しボタンを押してからの時間。UIもあるとよい
                //最後にスコアを表示する、どっちが勝ったかの勝敗を付ける。
                //タイトル画面に戻り、もう一度できるようにする
                //個人個人のプロフィールを設定させ、終わっても、また、戻ってくるようにする。
                //一人が抜けたら、対戦相手が抜けました。と試合を終わらせて、タイトルに戻るようにする。
                if("player1" == myPlayerKey)
                {
                    //データベースにあるデータを読み込む
                    for(quizSnapshot in snapshot.children){
                        val question = quizSnapshot.child("question").getValue(String::class.java)
                        val answer = quizSnapshot.child("answer").getValue(String::class.java)
                        val choices = quizSnapshot.child("choices").children.map { it.getValue(String::class.java) }

                        val quiz = Quiz(
                            question ?: "質問なし",
                            answer ?: "答えなし",
                            choices.filterNotNull()
                        )
                        quizList.add(quiz)

                    }

                    quizList.shuffle()

                    Log.d("QuizList", "読み込んだクイズ数：${quizList.size}")

                    // データベースに上書き保存する用のMap
                    val updateMap = mutableMapOf<String, Any>()
                    quizList.forEachIndexed { index, quiz ->
                        //001,002とゼロ埋め
                        val quizId = String.format("quiz%03d", index + 1)
                        updateMap[quizId] = mapOf(
                            "question" to quiz.question,
                            "answer" to quiz.answer,
                            "choices" to quiz.choices
                        )
                    }

                    // 上書き保存
                    roomRef.child("ShullQuizList").setValue(updateMap)

                }

                //問題文を取り込んで、シャッフルできたかの確認
                for (quiznam in quizList)
                {
                    Log.d("QuizList", "クイズの答え：${quiznam.answer}")
                }

                showNextQuiz()

            }



            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizLoad", "読み込み失敗:${error.message}")
            }
        })

        if("player2" == myPlayerKey)
        {
            // player2 はそのルームの ShullQuizList を読む
            roomRef.child("ShullQuizList").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        quizList.clear()
                        for (quizSnapshot in snapshot.children) {
                            val question = quizSnapshot.child("question").getValue(String::class.java)
                            val answer = quizSnapshot.child("answer").getValue(String::class.java)
                            val choices = quizSnapshot.child("choices").children.map { it.getValue(String::class.java) }

                            val quiz = Quiz(
                                question ?: "質問なし",
                                answer ?: "答えなし",
                                choices.filterNotNull()
                            )
                            quizList.add(quiz)
                        }
                        showNextQuiz()

                        //読み込みが成功したら、リスナーを外す
                        roomRef.child("ShullQuizList").removeEventListener(this)
                    }

                }



                override fun onCancelled(error: DatabaseError) {
                    Log.e("QuizLoad", "読み込み失敗:${error.message}")
                }
            })
        }

        //早押しボタンを押した人
        roomRef.child("buzz").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val whoBuzzed  = snapshot.getValue(String::class.java)

                Log.d("buzz","buzzのイベントが呼ばれたよー")
                if (whoBuzzed != null)
                {
                    if (whoBuzzed == myPlayerKey)
                    {
                        //自分が押したとき
                        isenabledtrue()
                    }
                    else
                    {
                        //他の人が押したとき
                        allisenabledfalse()
                    }
                }
            }
            //他の人が正解したときどうなるのか、他の人が不正解だった時はどうなるのか。
            //（正解）次の問題に二人とも変わって、ボタンだけ表示されてる状態になる
            //（不正解）その人の回答権は無くなって、もう一人の回答になるのか、ボタンを押すまでか、後者の場合は、タイム制限が必要。どっちもか。

            override fun onCancelled(error: DatabaseError) {
                Log.d("buzz","変更が入りました。")
            }
        })

        //解答したか
        roomRef.child("answers").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val answers1 = snapshot.child("player1").getValue(String::class.java)
                val answers2 = snapshot.child("player2").getValue(String::class.java)

                if (answers1 == "wrong" && answers2 == "wrong")
                {
                    dialog()
                    //answrsは１問が終わるごとにリセットする。
                    roomRef.child("answers").setValue(null)
                    roomRef.child("answers").setValue(null)
                    //これで、ダイアログの下の選択肢が非表示になったかな
                    allisenabledfalse()
                }
                else if (answers1 == "correct" || answers2 == "correct")
                {
                    dialog()
                    roomRef.child("answers").setValue(null)
                    roomRef.child("answers").setValue(null)
                    //これで、ダイアログの下の選択肢が非表示になったかな
                    allisenabledfalse()
                }
                else
                {
                    //不正解だった時,選択権をなくして、相手側が答えるか、時間制限にする。
                    //間違えたほうが、選択肢がなくなって、相手側に移る方式とする。
                    roomRef.child("buzz").get().addOnSuccessListener { snapshot ->
                        val buzz = snapshot.getValue(String::class.java)//ダイアログのOKを押さないと次に進まないようにしないといけない。

                        if (buzz != null)
                        {
                            if (myPlayerKey == buzz)
                            {
                                //押した人の選択肢と早押しボタンが消える。
                                allisenabledfalse()
                            }
                            else
                            {
                                //誰かが間違えたら、強制的に他の人が解答になる。
                                isenabledtrue()
                            }
                        }

                        //不正解の時は、必ずここに来るのか？
                        roomRef.child("buzz").setValue(null)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("faker","誰が間違えたか、正解したか")
            }
        })

        //ダイアログのOKボタンを押したか
        roomRef.child("okPressed").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val player1Pressed = snapshot.child("player1").getValue(Boolean::class.java) ?: false
                val player2Pressed = snapshot.child("player2").getValue(Boolean::class.java) ?: false

                if (quizCount == QUIZ_COUNT) {
                    //ここで軽いファイルを宣言している。そして、データを保存している。
                    val prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE)
                    val editor = prefs.edit()
                    var totalScore = prefs.getInt(playerName + "_total_score", 0)
                    totalScore += score
                    editor.putInt(playerName + "_total_score", totalScore)
                    editor.apply()

                    val intent = Intent(
                        this@MainActivity,
                        activity_marutiresult::class.java
                    )
                    intent.putExtra("RIGHT_ANSWER_COUNT", rightAnswerCount)
                    intent.putExtra("SCORE", score)
                    intent.putExtra("playerName", playerName)
                    startActivity(intent)
                    finish()
                }

                //ダイアログの部分に解説を載せるなどの、気になっているうちに、勉強できるなどの利点をつぎ込むことが可能。
                if (player1Pressed && player2Pressed)
                {
                    // 両方押したので次の問題へ
                    snapshot.ref.setValue(null) // リセット
                    quizCount++

                    showNextQuiz()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        playerName = intent.getStringExtra("playerName")
        if (playerName == null) playerName = "ゲスト"



        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main)
                ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //選択肢達のクリックリスナーの設定
        binding!!.answerBtn1.setOnClickListener(this)
        binding!!.answerBtn2.setOnClickListener(this)
        binding!!.answerBtn3.setOnClickListener(this)
        binding!!.answerBtn4.setOnClickListener(this)

        //早押しボタンを押したときの処理
        binding!!.pushBtn.setOnClickListener { v:View? ->
            //isenabledtrue()
            roomRef.child("buzz").setValue(myPlayerKey)
        }

    }

    private var currentIndex = 0
    private var rightAnswer  = ""

    //okボタンを押したことを知らせる
    private fun onDialogOkPressed() {
        roomRef.child("okPressed").child(myPlayerKey).setValue(true)
    }

    //カウントダウンのスタート
    private fun startCountdown(remainingTime:Long)
    {
        CountDownTimer?.cancel()
        
        object : CountDownTimer(remainingTime, 1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                binding!!.timerLabel.text = "${millisUntilFinished / 1000}　秒"
            }

            override fun onFinish() {
                //タイムアップ処理
            }
        }.start()
    }

    //早押しボタン以外のボタンを非表示にする
    private fun isenabledfalse()
    {
        binding!!.pushBtn.visibility = View.VISIBLE
        binding!!.answerBtn1.visibility = View.INVISIBLE
        binding!!.answerBtn2.visibility = View.INVISIBLE
        binding!!.answerBtn3.visibility = View.INVISIBLE
        binding!!.answerBtn4.visibility = View.INVISIBLE
    }

    //早押しボタンを非表示にし、選択肢を表示する
    private fun isenabledtrue()
    {
        binding!!.pushBtn.visibility = View.INVISIBLE
        binding!!.answerBtn1.visibility = View.VISIBLE
        binding!!.answerBtn2.visibility = View.VISIBLE
        binding!!.answerBtn3.visibility = View.VISIBLE
        binding!!.answerBtn4.visibility = View.VISIBLE
    }

    //全部非表示にする
    private fun allisenabledfalse()
    {
        binding!!.pushBtn.visibility = View.INVISIBLE
        binding!!.answerBtn1.visibility = View.INVISIBLE
        binding!!.answerBtn2.visibility = View.INVISIBLE
        binding!!.answerBtn3.visibility = View.INVISIBLE
        binding!!.answerBtn4.visibility = View.INVISIBLE
    }

    private fun showNextQuiz()//早押しを作りたい。そのためには、wifiの問題をどうにかしないと
    {
        if (currentIndex >= quizList.size) return

        isenabledfalse()

        //問題番号
        binding!!.countLabel.text = getString(R.string.count_label, quizCount)

        //クイズ取り出し
        val quiz = quizList[currentIndex]
        binding!!.questionLabel.text = quiz.question
        rightAnswer = quiz.answer

        //正解を含めた選択肢をシャッフル
        val options = quiz.choices.toMutableList()
        options.add(rightAnswer)
        options.shuffle()

        binding!!.answerBtn1.text = options[0]
        binding!!.answerBtn2.text = options[1]
        binding!!.answerBtn3.text = options[2]
        binding!!.answerBtn4.text = options[3]

        currentIndex ++

        if (myPlayerKey == "player1")
        {
            val startTime = System.currentTimeMillis()
            roomRef.child("startTime").setValue(startTime)
        }

        //データベースにある、基準時間を習得
        roomRef.child("startTime").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val startTime = snapshot.getValue(Long::class.java) ?:return
                val now = System.currentTimeMillis()
                val elapsed = now - startTime //どのくらい遅れて受け取ったか

                val remainingTime = TOTAL_TIME - elapsed
                startCountdown(remainingTime)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    override fun onClick(v: View) {
        val answerBtn = v as Button
        val btnText = answerBtn.text.toString()

        if (btnText == rightAnswer) {
            alertTitle = "正解!"
            correctStreak++
            score += correctStreak
            rightAnswerCount++//勝敗どうやってつけよう。（スコア、時間制限、）
            roomRef.child("answers").child(myPlayerKey).setValue("correct")//二個下のプレイヤーかのどっちかをやろうと思ったが、どっちのプレイヤーに生後判定を入れるのかが、わからない
        } else {
            alertTitle = "不正解..."
            correctStreak = 0
            roomRef.child("answers").child(myPlayerKey).setValue("wrong")
            //playerの下にanswersを作ることに成功した。まだ、ボタンは反応しない。
            //answersを変えたときにちゃんと呼ばれているのかの確認と、間違えたときは、強制的に相手に回答権が映るようにするか？
        }

        //dialog()

    }

    companion object {
        private const val QUIZ_COUNT = 10
    }

    private fun dialog()//誰かが正解、全員が不正解の時に表示したい
    {
        MaterialAlertDialogBuilder(this)
            .setTitle(alertTitle)
            .setMessage("答え : $rightAnswer")
            .setPositiveButton(
                "OK"
            ) { dialogInterface, i ->
                //dialogのOKボタンを押したら
                onDialogOkPressed()
            }
            .setCancelable(false)
            .show()
    }
}
