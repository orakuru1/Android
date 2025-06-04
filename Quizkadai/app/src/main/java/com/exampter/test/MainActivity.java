package com.exampter.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.exampter.test.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    private int rightAnswerCount = 0;
    private int quizCount = 1;
    private String rightAnswer;
    private static final int QUIZ_COUNT = 10;
    private int score = 0;
    private int correctStreak = 0;

    private final ArrayList<ArrayList<String>> quizArray = new ArrayList<>();
    private final String[][] quizData = {
            {"「I’ll Kill You」と「I’ll Kill You」は彼の最も有名な曲です、メグミ。", "呪術廻戦", "長崎市", "福島市", "前橋市"},
            {"走らないで、走らないで、走らないで。", "エヴァンゲリオン", "広島市", "甲府市", "岡山市"},
            {"たとえ彼が邪魔をしたり、裏切ったり、私が世間知らずだと言ったとしても、私は彼を信じたかったのです！", "約束のネバーランド", "大分市", "秋田市", "福岡市"},
            {"主人公の仕事は、人生という冒険の中で偉業を成し遂げることです。", "僕のヒーローアカデミア", "水戸市", "岐阜市", "福井市"},
            {"私は二度逮捕されました。父は一度も私を叩いたことがありません!", "機動戦士ガンダム", "横浜市", "鳥取市", "仙台市"},
            {"痩せたいならこれを食べなさい！！！", "バキ", "青森市", "山口市", "奈良市"},
            {"質問に質問で答えないでください!!生徒たちに質問に質問で答えることを教えているのでしょうか?", "ジョジョの奇妙な冒険", "盛岡市", "新宿区", "京都市"},
            {"生きるか死ぬかを他人に決めさせないでください!", "鬼滅の刃", "金沢市", "名古屋市", "奈良市"},
            {"また会ったら友達と呼んでくれますか？", "ONE PIECE", "札幌市", "岡山市", "奈良市"},
            {"包帯を巻いてくれてありがとう", "進撃の巨人", "福岡市", "松江市", "福井市"},
            {"めっちゃ寒いです…！", "カイジ", "長崎市", "福島市", "前橋市"},
            {"しかし、私のような凡人には下を向いている暇があるだろうか？", "ハイキュー!!", "広島市", "甲府市", "岡山市"},
            {"不思議！ 「愛している」…知りたい", "ヴァイオレット・エヴァーガーデン", "大分市", "秋田市", "福岡市"},
            {"これはジェッドアームストロングの上にニールアームストロングが乗っているのでしょうか？", "銀魂", "水戸市", "岐阜市", "福井市"},
            {"止まったよ────止まれ！", "NARUTO -ナルト-", "横浜市", "鳥取市", "仙台市"},
            {"立ち上がって、歩いて、前へ進みましょう。あなたの足は大丈夫です。", "鋼の錬金術師", "青森市", "山口市", "奈良市"},
            {"彼は新世界の神となった。", "DEATH NOTE -デスノート-", "盛岡市", "新宿区", "京都市"},
            {"君は死んだ！", "北斗の拳", "金沢市", "名古屋市", "奈良市"},
            {"さて、これが最後です。頑張って下さい。", "HUNTER×HUNTER", "札幌市", "岡山市", "奈良市"},
            {"十分な経験があれば、弱者でもエリートに勝つことができます。", "ドラゴンボール", "福岡市", "松江市", "福井市"},
    };

    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playerName = getIntent().getStringExtra("playerName");
        if (playerName == null) playerName = "ゲスト";

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.answerBtn1.setOnClickListener(this);
        binding.answerBtn2.setOnClickListener(this);
        binding.answerBtn3.setOnClickListener(this);
        binding.answerBtn4.setOnClickListener(this);

        for (String[] quizDatum : quizData) {
            ArrayList<String> tmpArray = new ArrayList<>();
            Collections.addAll(tmpArray, quizDatum);
            quizArray.add(tmpArray);
        }

        Collections.shuffle(quizArray);
        showNextQuiz();
    }

    private void showNextQuiz() {
        binding.countLabel.setText(getString(R.string.count_label, quizCount));

        ArrayList<String> quiz = new ArrayList<>(quizArray.remove(0));
        binding.questionLabel.setText(quiz.get(0));

        rightAnswer = quiz.get(1);
        quiz.remove(0);
        quiz.remove(0);

        quiz.add(rightAnswer);
        Collections.shuffle(quiz);

        binding.answerBtn1.setText(quiz.get(0));
        binding.answerBtn2.setText(quiz.get(1));
        binding.answerBtn3.setText(quiz.get(2));
        binding.answerBtn4.setText(quiz.get(3));
    }

    @Override
    public void onClick(View v) {
        Button answerBtn = (Button) v;
        String btnText = answerBtn.getText().toString();

        String alertTitle;
        if (btnText.equals(rightAnswer)) {
            alertTitle = "正解!";
            correctStreak++;
            score += correctStreak;
            rightAnswerCount++;
        } else {
            alertTitle = "不正解...";
            correctStreak = 0;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(alertTitle)
                .setMessage("答え : " + rightAnswer)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (quizCount == QUIZ_COUNT) {
                            SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            int totalScore = prefs.getInt(playerName + "_total_score", 0);
                            totalScore += score;
                            editor.putInt(playerName + "_total_score", totalScore);
                            editor.apply();

                            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                            intent.putExtra("RIGHT_ANSWER_COUNT", rightAnswerCount);
                            intent.putExtra("SCORE", score);
                            intent.putExtra("playerName", playerName);
                            startActivity(intent);
                            finish();
                        } else {
                            quizCount++;
                            showNextQuiz();
                        }
                    }
                })
                .setCancelable(false)
                .show();
    }
}
