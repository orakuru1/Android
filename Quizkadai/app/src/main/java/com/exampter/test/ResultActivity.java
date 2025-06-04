package com.exampter.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.exampter.test.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityResultBinding binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int score = getIntent().getIntExtra("RIGHT_ANSWER_COUNT", 0);
        String playerName = getIntent().getStringExtra("playerName");
        if (playerName == null) {
            playerName = "ゲスト";
        }

        SharedPreferences prefs = getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        int totalScore = prefs.getInt(playerName + "_total_score", 0);
        totalScore += score;

        // 表示
        binding.resultLabel.setText(getString(R.string.result_score, score));
        binding.totalScoreLabel.setText(getString(R.string.result_total_score, totalScore));

        // 保存
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(playerName + "_total_score", totalScore);
        editor.apply();

        Button returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, TitleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
