package com.exampter.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TitleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            // 名前入力なしで「ゲスト」として開始
            String playerName = "ゲスト";
            Intent intent = new Intent(TitleActivity.this, MainActivity.class);
            intent.putExtra("playerName", playerName);
            startActivity(intent);
            finish();
        });
    }
}
